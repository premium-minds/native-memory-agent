package com.premiumminds.jvm.agent.nativememory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * inspired by
 * <a href="https://github.com/turf00/jvm-native-memory-parse/blob/master/src/main/kotlin/com/bvb/jvm/AppNativeMemParse.kt">AppNativeMemParse.kt</a>
 */
public class NativeMemoryParser {

	private static final Pattern PATTERN_INDIVIDUAL_TYPES = Pattern.compile("^-\\s+(.+)\\s\\(reserved=(\\d+)KB, committed=(\\d+)KB.+");
	private static final Pattern PATTERN_TOTAL = Pattern.compile("^Total: reserved=(\\d+)KB, committed=(\\d+)KB");

	public List<Memory> parse(String cenas){
		final String[] split = cenas.split("\n");

		LineMatcher lineMatcher = new LineMatcher();

		return Arrays.stream(split)
					 .sequential()
					 .map(lineMatcher::parseLine)
					 .filter(Optional::isPresent)
					 .map(Optional::get)
					.collect(Collectors.toList());

	}

	private enum Position
	{
		BEFORE_TOTAL,
		AFTER_TOTAL,
	}

	private static class LineMatcher {

		private Position position = Position.BEFORE_TOTAL;

		public Optional<Memory> parseLine(String l) {
			switch (position){
				case AFTER_TOTAL:
					{
						final Matcher matcher = PATTERN_INDIVIDUAL_TYPES.matcher(l);
						if (matcher.matches()){
							return Optional.of(new Memory(
								matcher.group(1),
								Long.parseLong(matcher.group(2)),
								Long.parseLong(matcher.group(3))));
						}
					}
					break;
				case BEFORE_TOTAL:
					{
						final Matcher matcher = PATTERN_TOTAL.matcher(l);
						if (matcher.matches()) {
							position = Position.AFTER_TOTAL;
							return Optional.of(new Memory(
								"Total",
								Long.parseLong(matcher.group(1)),
								Long.parseLong(matcher.group(2))));
						}
					}
					break;
			}
			return Optional.empty();
		}

	}

	public static class Memory {

		final String name;
		final Long reserved;
		final Long committed;

		public Memory(final String name, final Long reserved, final Long committed) {
			this.name = name;
			this.reserved = reserved;
			this.committed = committed;
		}
	}
}


