package com.roadtojava.benchmarks;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@Fork(1)
@State(Scope.Thread)
public class StringBuildingBenchmark {
    @Param({"10", "100"}) public int count;

    @Benchmark
    public String builder() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) result.append(i);
        return result.toString(); // returning prevents dead-code elimination of the result
    }

    @Benchmark
    public String concatenation() {
        String result = "";
        for (int i = 0; i < count; i++) result += i;
        return result;
    }
}
