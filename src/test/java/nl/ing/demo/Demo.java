package nl.ing.demo;



import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
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
public class Demo
{



    @Test
    public void spaghetti()
    {
        /*
            --------On Startup-------------
         */
        BusinessLogic businessLogic = new BusinessLogic("user@example.com", "10 Herengracht", "00-18987");

        /*
            ------- Runtime Business Logic ----------
         */

        final AtomicLong delta = new AtomicLong(0L);
        IntStream.range(1, 1400).mapToObj(i -> "User Name-" + i)
                .map(
                        name -> {
                            delta.set(System.nanoTime());
                            String format = name.substring(name.length() - 1);

                            switch (format) {
                                case "1" : {
                                    String name_1 = businessLogic.appendPostalAddress(name);
                                    String name_2 = businessLogic.appendEmail(name_1);
                                    String name_3 = businessLogic.appendPhoneNumber(name_2);
                                    return name_3;
                                }
                                case "2" : {
                                    String name_1 = businessLogic.appendEmail(name);
                                    String name_2 = businessLogic.appendPhoneNumber(name_1);
                                    String name_3 = businessLogic.appendPostalAddress(name_2);
                                    return name_3;
                                }
                                case "3" : {
                                    String name_1 = businessLogic.appendPhoneNumber(name);
                                    String name_2 = businessLogic.appendEmail(name_1);
                                    return name_2;
                                }
                                default: return name;
                            }

                        }
                ).forEach(name -> System.out.println(" Result : " + name + " Delta : " + (System.nanoTime() - delta.get())));

    }


    @Test
    public void withChain()
    {
        /*
            --------On Startup-------------
         */
        List<String> format_1 = List.of("postalAddress", "email", "phone");
        List<String> format_2 = List.of("email", "phone", "postalAddress");
        List<String> format_3 = List.of("phone", "email");


        BusinessLogic appenders = new BusinessLogic("user@example.com", "10 Herengracht", "00-18987");

        Map<String, Function<String, String>> functionMap =
                Map.of(
                        "phone", appenders::appendPhoneNumber,
                        "email", appenders::appendEmail,
                        "postalAddress", appenders::appendPostalAddress
                );

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
        IntStream.range(1, 14000).mapToObj(i -> "User Name-" + i)
                .map(
                        name -> {
                            delta.set(System.nanoTime());
                            return chainMap.getOrDefault(
                                    name.substring(name.length() - 1),
                                    Function.identity()
                            ).apply(name);

                        }
                ).forEach(name -> System.out.println(" Result : " + name + " Delta : " + (System.nanoTime() - delta.get())));

    }
}
