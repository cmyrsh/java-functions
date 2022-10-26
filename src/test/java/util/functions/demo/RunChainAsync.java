package util.functions.demo;



import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Business Logic
 * ---------------------------------------------------------------------------------------------------------------------
 * Based on type of customer, append email, postalAddress and phone number to customer data.
 * ---------------------------------------------------------------------------------------------------------------------
 * Lets assume that type of customer is derived from last letter in the payload.
 *
 * If payload text ends with 1 then append postalAddress, then email and then phone Number
 * If payload text ends with 2 then append email, then phone number and then postal address
 * If payload text ends with 3 then append phone Number, then email. Do not append postal address.
 * For everything else, do not append any attribute.
 * ---------------------------------------------------------------------------------------------------------------------
 *
 */
public class RunChainAsync
{


    public static void main(String[] args) {
        new RunChainAsync().process(15000);
    }



    public void process(Integer limit)
    {
        /*
            --------On Startup-------------
         */
        List<String> format_1 = List.of("postalAddress", "email", "phone");
        List<String> format_2 = List.of("email", "phone", "postalAddress");
        List<String> format_3 = List.of("phone", "email");


        BusinessLogicAsync logic = new BusinessLogicAsync("user@example.com", "10 Herengracht", "00-18987");

        Map<String, Function<String, CompletableFuture<String>>> functionMap =
                Map.of(
                        "phone", logic::appendPhoneNumber,
                        "email", logic::appendEmail,
                        "postalAddress", logic::appendPostalAddress
                );

        ChainBuilder<String> stringFunctions = new ChainBuilder<>();

        Function<String, CompletableFuture<String>> fmt_1 = stringFunctions.buildAsyncChain(format_1, functionMap);
        Function<String, CompletableFuture<String>> fmt_2 = stringFunctions.buildAsyncChain(format_2, functionMap);
        Function<String, CompletableFuture<String>> fmt_3 = stringFunctions.buildAsyncChain(format_3, functionMap);


        Map<String, Function<String, CompletableFuture<String>>> chainMap =
                Map.of(
                        "1", fmt_1,
                        "2", fmt_2,
                        "3", fmt_3
                );
        /*
            ------- Runtime Business Logic ----------
         */
        final AtomicLong delta = new AtomicLong(0L);
        IntStream.range(1, limit).mapToObj(i -> "User Name-" + i)
                .map(
                        name -> {
                            delta.set(System.nanoTime());
                            String result = chainMap.getOrDefault(
                                    name.substring(name.length() - 1),
                                    s -> CompletableFuture.completedFuture(s)
                            ).apply(name).join();
                            delta.set(System.nanoTime() - delta.get());
                            return result;

                        }
                ).filter(name -> name.contains("email")).forEach(name -> System.out.println(" Result : " + name + " -- Delta : " + delta.get()));

    }
}
