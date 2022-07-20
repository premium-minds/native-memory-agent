package com.premiumminds.jvm.agent.nativememory;

import com.premiumminds.jvm.agent.nativememory.NativeMemoryParser.Memory;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.SocketException;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class Main {

	private static final int PERIOD_SECONDS = 5;
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, (Runnable r) -> {
		Thread t = new Thread(r);
		t.setName("native-memory-statistics-scheduler");
		t.setDaemon(true);
		return t;
	});

	/**
	 *
	 * @param agentArgs host:port,key:value,key:value
	 * 					example: 'localhost:8125,foo:bar'
	 */
	public static void premain(String agentArgs, Instrumentation inst)
		throws SocketException, ExecutionException, InterruptedException
	{

		if (agentArgs == null || agentArgs.isEmpty()){
			System.err.println("no host and port configured. ex: localhost:8125");
			return;
		}
		final String[] args = agentArgs.split(",");

		final String[] address = args[0].split(":");
		if (address.length != 2){
			System.err.println("invalid host and port configuration. ex: localhost:8125");
			return;
		}
		final StringJoiner tags = new StringJoiner(",");
		for (int i = 1; i < args.length; i++) {
			tags.add(args[i]);
		}

		MetricConverter metricConverter = new MetricConverter(tags.toString());

		NativeMemoryParser nmtParser = new NativeMemoryParser();
		StatsDSender statsDSender = new StatsDSender(address[0], Integer.parseInt(address[1]));

		if( isEnabled() ){
			scheduler.scheduleAtFixedRate(() -> {
				try {
					final String vmNativeMemory = getVmNativeMemory();

					final List<Memory> memoryList = nmtParser.parse(vmNativeMemory);
					memoryList.stream().flatMap(x -> metricConverter.toStatsD(x).stream()).forEach(x -> {
						try {
							statsDSender.sendEcho(x);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});

				} catch (MalformedObjectNameException | ReflectionException | InstanceNotFoundException |
						 MBeanException e) {
					throw new RuntimeException(e);
				}
			}, PERIOD_SECONDS, PERIOD_SECONDS, TimeUnit.SECONDS);
		} else {
			System.err.println("NativeMemoryTracking is not enabled. Use -XX:NativeMemoryTracking=[off | summary | detail]");
		}
	}

	private static boolean isEnabled() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		List<String> jvmArgs = runtimeMXBean.getInputArguments();

		return jvmArgs.stream().filter(arg -> arg.matches("-XX:NativeMemoryTracking=.*")).findFirst().map(x -> {
			switch (x) {
				case "-XX:NativeMemoryTracking=summary":
				case "-XX:NativeMemoryTracking=detail":
					return true;
				default:
					return false;
			}
		}).orElse(false);

	}

	private static String getVmNativeMemory()
		throws MalformedObjectNameException, ReflectionException, InstanceNotFoundException, MBeanException
	{
		return (String)ManagementFactory.getPlatformMBeanServer().invoke(
			new ObjectName("com.sun.management:type=DiagnosticCommand"),
			"vmNativeMemory",
			new Object[] { new String[] { "summary" } },
			new String[] { "[Ljava.lang.String;" });

	}
}
