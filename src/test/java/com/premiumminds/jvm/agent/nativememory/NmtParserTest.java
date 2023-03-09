package com.premiumminds.jvm.agent.nativememory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import com.premiumminds.jvm.agent.nativememory.NativeMemoryParser.Memory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NmtParserTest {

	@Test
	public void parseNmtAndConstructStatsDMetrics() throws IOException {

		String nmtContent = getNmtContent();

		NativeMemoryParser nmtParser = new NativeMemoryParser();
		final List<Memory> memoryList = nmtParser.parse(nmtContent);

		MetricConverter metricConverter = new MetricConverter("key1,foo1");

		final String statdsOutput = memoryList.stream()
				.flatMap((Memory memory) -> metricConverter.toStatsD(memory).stream())
				.collect(Collectors.joining("\n"));

		Assertions.assertEquals(
	"jvm.nmt.total.reserved:4536253|g|#key1,foo1\n" +
			"jvm.nmt.total.committed:118721|g|#key1,foo1\n" +
			"jvm.nmt.java_heap.reserved:3018752|g|#key1,foo1\n" +
			"jvm.nmt.java_heap.committed:28672|g|#key1,foo1\n" +
			"jvm.nmt.class.reserved:1048752|g|#key1,foo1\n" +
			"jvm.nmt.class.committed:624|g|#key1,foo1\n" +
			"jvm.nmt.thread.reserved:24640|g|#key1,foo1\n" +
			"jvm.nmt.thread.committed:1276|g|#key1,foo1\n" +
			"jvm.nmt.code.reserved:247815|g|#key1,foo1\n" +
			"jvm.nmt.code.committed:7679|g|#key1,foo1\n" +
			"jvm.nmt.gc.reserved:173043|g|#key1,foo1\n" +
			"jvm.nmt.gc.committed:62083|g|#key1,foo1\n" +
			"jvm.nmt.compiler.reserved:213|g|#key1,foo1\n" +
			"jvm.nmt.compiler.committed:213|g|#key1,foo1\n" +
			"jvm.nmt.internal.reserved:212|g|#key1,foo1\n" +
			"jvm.nmt.internal.committed:212|g|#key1,foo1\n" +
			"jvm.nmt.other.reserved:22|g|#key1,foo1\n" +
			"jvm.nmt.other.committed:22|g|#key1,foo1\n" +
			"jvm.nmt.symbol.reserved:1494|g|#key1,foo1\n" +
			"jvm.nmt.symbol.committed:1494|g|#key1,foo1\n" +
			"jvm.nmt.native_memory_tracking.reserved:381|g|#key1,foo1\n" +
			"jvm.nmt.native_memory_tracking.committed:381|g|#key1,foo1\n" +
			"jvm.nmt.shared_class_space.reserved:12288|g|#key1,foo1\n" +
			"jvm.nmt.shared_class_space.committed:12160|g|#key1,foo1\n" +
			"jvm.nmt.arena_chunk.reserved:188|g|#key1,foo1\n" +
			"jvm.nmt.arena_chunk.committed:188|g|#key1,foo1\n" +
			"jvm.nmt.tracing.reserved:32|g|#key1,foo1\n" +
			"jvm.nmt.tracing.committed:32|g|#key1,foo1\n" +
			"jvm.nmt.logging.reserved:5|g|#key1,foo1\n" +
			"jvm.nmt.logging.committed:5|g|#key1,foo1\n" +
			"jvm.nmt.arguments.reserved:8|g|#key1,foo1\n" +
			"jvm.nmt.arguments.committed:8|g|#key1,foo1\n" +
			"jvm.nmt.module.reserved:158|g|#key1,foo1\n" +
			"jvm.nmt.module.committed:158|g|#key1,foo1\n" +
			"jvm.nmt.safepoint.reserved:8|g|#key1,foo1\n" +
			"jvm.nmt.safepoint.committed:8|g|#key1,foo1\n" +
			"jvm.nmt.synchronization.reserved:35|g|#key1,foo1\n" +
			"jvm.nmt.synchronization.committed:35|g|#key1,foo1\n" +
			"jvm.nmt.serviceability.reserved:1|g|#key1,foo1\n" +
			"jvm.nmt.serviceability.committed:1|g|#key1,foo1\n" +
			"jvm.nmt.metaspace.reserved:8204|g|#key1,foo1\n" +
			"jvm.nmt.metaspace.committed:3468|g|#key1,foo1\n" +
			"jvm.nmt.string_deduplication.reserved:1|g|#key1,foo1\n" +
			"jvm.nmt.string_deduplication.committed:1|g|#key1,foo1",
			statdsOutput);

	}

	private String getNmtContent() throws IOException {
		try (InputStream inputStream = getClass().getResourceAsStream("/nmt.txt")) {
			return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines()
					.collect(Collectors.joining("\n"));
		}
	}
}
