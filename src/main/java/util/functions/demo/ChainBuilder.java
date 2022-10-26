package util.functions.demo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Builds chain from set of different functions
 *
 */
public class ChainBuilder<T>
{

    public Function<T, T> buildChain(List<String> calls, Map<String, Function<T, T>> functionMap) {
        return calls.stream()
                .map(call -> functionMap.get(call))
                .reduce((collected, next) -> collected.andThen(next))
                .orElseThrow(() -> new IllegalArgumentException("Could not find functions matching " + calls));
    }


    public Function<T, CompletableFuture<T>> buildAsyncChain(List<String> calls, Map<String, Function<T, CompletableFuture<T>>> functionMap) {
        return calls.stream()
                .map(call -> functionMap.get(call))
                .reduce(
                        (collected, next) -> collected.andThen(
                        tCompletableFuture -> tCompletableFuture.thenCompose(t -> next.apply(t))
                    )
                )
                .orElseThrow(() -> new IllegalArgumentException("Could not find functions matching " + calls));
    }

}
