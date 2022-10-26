package nl.ing.demo;



import java.util.concurrent.atomic.AtomicLong;
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
public class RunSpaghettiAsync
{


    public static void main(String[] args) {
        new RunSpaghettiAsync().process(15000);
    }

    public void process(Integer limit)
    {
        /*
            --------On Startup-------------
         */
        BusinessLogicAsync logic = new BusinessLogicAsync("user@example.com", "10 Herengracht", "00-18987");

        /*
            ------- Runtime Business Logic ----------
         */

        final AtomicLong delta = new AtomicLong(0L);
        IntStream.range(1, limit).mapToObj(i -> "User Name-" + i)
                .map(
                        name -> {
                            delta.set(System.nanoTime());
                            String format = name.substring(name.length() - 1);

                            switch (format) {
                                case "1" : {
                                    String name_3 = logic.appendPostalAddress(name)
                                            .thenCompose(logic::appendEmail)
                                            .thenCompose(logic::appendPhoneNumber).join();
                                    delta.set(System.nanoTime() - delta.get());
                                    return name_3;
                                }
                                case "2" : {

                                    String name_3 = logic.appendEmail(name)
                                            .thenCompose(logic::appendPhoneNumber)
                                            .thenCompose(logic::appendPostalAddress)
                                                    .join();
                                    delta.set(System.nanoTime() - delta.get());
                                    return name_3;
                                }
                                case "3" : {
                                    String name_2 = logic.appendPhoneNumber(name)
                                                    .thenCompose(logic::appendEmail).join();
                                    delta.set(System.nanoTime() - delta.get());
                                    return name_2;
                                }
                                default: {
                                    delta.set(System.nanoTime() - delta.get());
                                    return name;
                                }
                            }


                        }
                ).filter(name -> name.contains("email")).forEach(name -> System.out.println(" Result : " + name + " -- Delta : " + delta.get()));

    }


}
