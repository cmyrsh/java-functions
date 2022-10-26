package util.functions.demo;



import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
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
public class RunChain
{


    public static void main(String[] args) {
        new RunChain().process(10_000);
    }



    public void process(Integer limit)
    {
        /*
            --------On Startup-------------
         */


        // -------- Define Business Logic and register Business Functions with names --------------
        BusinessLogic logic = new BusinessLogic("user@example.com", "10 Herengracht", "00-18987");

        Map<String, Function<String, String>> functionMap =
                Map.of(
                        "phone", logic::appendPhoneNumber,
                        "email", logic::appendEmail,
                        "postalAddress", logic::appendPostalAddress
                );


        // ---------- Define Formats -----------------
        List<String> format_1 = List.of("postalAddress", "email", "phone");
        List<String> format_2 = List.of("email", "phone", "postalAddress");
        List<String> format_3 = List.of("phone", "email");


        // ------------- Create Chains Early -----------------------------
        ChainBuilder<String> stringFunctions = new ChainBuilder<>();

        Function<String, String> fmt_1 = stringFunctions.buildChain(format_1, functionMap);
        Function<String, String> fmt_2 = stringFunctions.buildChain(format_2, functionMap);
        Function<String, String> fmt_3 = stringFunctions.buildChain(format_3, functionMap);


        Map<String, Function<String, String>> chainMap =
                Map.of(
                        "1", fmt_1,
                        "2", fmt_2,
                        "3", fmt_3
                );
        /*
            ------- Runtime Business Logic ----------
         */
        final AtomicLong delta = new AtomicLong(0L);
        String all_results = IntStream.range(1, limit).mapToObj(i -> "User Name-" + i)
                .map(
                        name -> {
                            String format = name.substring(name.length() - 1);
                            delta.set(System.nanoTime());
                            String result = chainMap.getOrDefault(
                                    format,
                                    Function.identity()
                            ).apply(name);
                            delta.set(System.nanoTime() - delta.get());
                            return result;

                        }
                )
                .filter(name -> name.contains("email"))
                .map(name -> name.concat("--").concat(Long.toString(delta.get())))
                .collect(Collectors.joining("\n"));
        System.out.println(all_results);
    }
}
