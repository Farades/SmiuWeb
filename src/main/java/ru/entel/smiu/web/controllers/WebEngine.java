package ru.entel.smiu.web.controllers;


import ru.entel.smiu.datadealer.engine.Engine;
import ru.entel.smiu.datadealer.protocols.service.ProtocolMaster;
import ru.entel.smiu.datadealer.protocols.service.ProtocolSlave;
import ru.entel.smiu.web.db.entity.Device;
import ru.entel.smiu.web.devices.WebDevice;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@ManagedBean(name = "webEngine", eager = true)
@ApplicationScoped
public class WebEngine {
    private Engine engine;
    private boolean dataEngineStatus = false;

    private Map<Device, WebDevice> allDevices = new HashMap<>();

    public WebEngine() {

//        engine.run();
    }

    @PostConstruct
    private void init() {
        System.out.println("--------WebEngine Start 222----------");
        initNativeLib();
        engine = new Engine();
        engine.configure();

        for (ProtocolMaster master : engine.getProtocolMasterMap().values()) {
            for (ProtocolSlave slave : master.getSlaves().values()) {
                if (allDevices.containsKey(slave.getDevice())) {
                    WebDevice webDevice = allDevices.get(slave.getDevice());
                    webDevice.addChannel(slave);
                } else {
                    WebDevice webDevice = new WebDevice(slave.getDevice());
                    webDevice.addChannel(slave);
                    allDevices.put(slave.getDevice(), webDevice);
                }
            }
        }
        System.out.println("---------------------------------------");
        System.out.println(allDevices);
    }

    private void initNativeLib() {
        final Field sysPathsField;
        try {
            sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void changeDataEngineStatus() {
        if (dataEngineStatus == true) {
            engine.run();
        } else {
            engine.stopEngine();
        }
    }

    public boolean isDataEngineStatus() {
        return dataEngineStatus;
    }

    public void setDataEngineStatus(boolean dataEngineStatus) {
        this.dataEngineStatus = dataEngineStatus;
    }

    public Map<Device, WebDevice> getAllDevices() {
        return allDevices;
    }

    public Engine getEngine() {
        return engine;
    }
}
