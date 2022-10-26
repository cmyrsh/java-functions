package nl.ing.demo;



import java.util.concurrent.atomic.AtomicLong;
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
public class RunSpaghetti
{


    public static void main(String[] args) {
        new RunSpaghetti().process(10_000);
    }

    public void process(Integer limit)
    {
        /*
            --------On Startup-------------
         */
        BusinessLogic logic = new BusinessLogic("user@example.com", "10 Herengracht", "00-18987");

        /*
            ------- Runtime Business Logic ----------
         */

        final AtomicLong delta = new AtomicLong(0L);
        String all_results = IntStream.range(1, limit).mapToObj(i -> "User Name-" + i)
                .map(
                        name -> {
                            delta.set(System.nanoTime());
                            String format = name.substring(name.length() - 1);

                            switch (format) {
                                case "1": {
                                    String name_1 = logic.appendPostalAddress(name);
                                    String name_2 = logic.appendEmail(name_1);
                                    String name_3 = logic.appendPhoneNumber(name_2);
                                    delta.set(System.nanoTime() - delta.get());
                                    return name_3;
                                }
                                case "2": {
                                    String name_1 = logic.appendEmail(name);
                                    String name_2 = logic.appendPhoneNumber(name_1);
                                    String name_3 = logic.appendPostalAddress(name_2);
                                    delta.set(System.nanoTime() - delta.get());
                                    return name_3;
                                }
                                case "3": {
                                    String name_1 = logic.appendPhoneNumber(name);
                                    String name_2 = logic.appendEmail(name_1);
                                    delta.set(System.nanoTime() - delta.get());
                                    return name_2;
                                }
                                default: {
                                    delta.set(System.nanoTime() - delta.get());
                                    return name;
                                }
                            }


                        }
                ).filter(name -> name.contains("email"))
                .map(name -> name.concat("--").concat(Long.toString(delta.get())))
                .collect(Collectors.joining("\n"));
        System.out.println(all_results);

    }


}
