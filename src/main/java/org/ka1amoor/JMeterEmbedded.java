package org.ka1amoor;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class JMeterEmbedded {

    public static void run(
            String jmeterHome,
            Path jmxPath,
            Path jtlPath,
            String host,
            String port,
            String path,
            String threads,
            String rampUp,
            String loops
    ) throws Exception {


        Files.createDirectories(jtlPath.toAbsolutePath().getParent());

        //  инициализация JMeter
        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.loadJMeterProperties(jmeterHome + "/bin/jmeter.properties");
        JMeterUtils.initLocale();

        //  properties, которые читает ${__P(...)} в JMX
        JMeterUtils.setProperty("jtl", jtlPath.toAbsolutePath().toString());

        JMeterUtils.setProperty("host", host);
        JMeterUtils.setProperty("port", port);
        JMeterUtils.setProperty("path", path);
        JMeterUtils.setProperty("threads", threads);
        JMeterUtils.setProperty("rampUp", rampUp);
        JMeterUtils.setProperty("loops", loops);

        // загрузка тест-плана .jmx
        SaveService.loadProperties();
        HashTree testPlanTree = SaveService.loadTree(new File(jmxPath.toString()));

        // старт
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        jmeter.configure(testPlanTree);
        jmeter.run();
    }
}
