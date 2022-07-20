package com.premiumminds.jvm.agent.nativememory;

import com.premiumminds.jvm.agent.nativememory.NativeMemoryParser.Memory;
import java.util.Arrays;
import java.util.List;

public class MetricConverter {

	private final String tags;

	public MetricConverter(final String tags) {
		this.tags = tags;
	}

	public List<String> toStatsD(Memory memory){
		String metric = "jvm.nmt." + memory.name.toLowerCase().replace(" ", "_");
		if (tags == null || tags.isEmpty()){
			return Arrays.asList(metric + ".reserved:" + memory.reserved + "|c",
								 metric + ".committed:" + memory.committed + "|c");
		} else {
			return Arrays.asList(metric + ".reserved:" + memory.reserved + "|c|#" + tags,
								 metric + ".committed:" + memory.committed + "|c|#"+ tags);
		}
	}

}
