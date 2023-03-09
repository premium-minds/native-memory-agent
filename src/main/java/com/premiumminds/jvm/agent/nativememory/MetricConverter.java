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
			return Arrays.asList(metric + ".reserved:" + memory.reserved + "|g",
								 metric + ".committed:" + memory.committed + "|g");
		} else {
			return Arrays.asList(metric + ".reserved:" + memory.reserved + "|g|#" + tags,
								 metric + ".committed:" + memory.committed + "|g|#"+ tags);
		}
	}

}
