package org.example;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MINUTES)
@Warmup(iterations = 1, time = 50, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 1, time = 50, timeUnit = TimeUnit.MILLISECONDS)
public class AlgorithmicSearch {

    @Param({"10", "100", "1000", "10000"})
    int arraySize;

    @Param({"0", "50", "99", "999", "9999"})
    int searchValue;

    int[] values;

    @Setup
    public void setup() {
        values = ThreadLocalRandom.current().ints(arraySize).toArray();
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    public void testSearchIndexFromFor(Blackhole bh) {
        int result = searchIndexFromFor(values, searchValue);
        bh.consume(result);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    public void testSearchIndexFromHash(Blackhole bh) throws InterruptedException {
        int result = searchIndexFromHash(values, searchValue);
        bh.consume(result);
    }

    public int searchIndexFromFor(int[] values, int search){
        for (var i = 0; i < values.length; i++){
            if (values[i] == search){
                return i;
            }
        }
        return -1;
    }

    public int searchIndexFromHash(int[] values, int search) throws InterruptedException {
        Map<Integer, Integer> valuesMap = IntStream.range(0, values.length)
            .boxed()
            .collect(Collectors.toMap(i ->  values[i], i -> i, (a,b) -> b , LinkedHashMap::new));
        if (valuesMap.containsValue(search)){
            return valuesMap.get(search);
        }
        return -1;
    }

}
