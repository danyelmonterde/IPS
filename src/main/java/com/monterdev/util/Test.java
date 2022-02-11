package com.monterdev.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {

    public static void main(String[] args) {
        String nameOS = "os.name";
        String versionOS = "os.version";
        String architectureOS = "os.arch";
        String userName = "user.name";
        String userHome = "user.home";
        String userDir = "user.dir";

        System.out.println("\n  The information about OS");
        System.out.println("\nName of the OS: " +
                System.getProperty(nameOS));
        System.out.println("Version of the OS: " +
                System.getProperty(versionOS));
        System.out.println("Architecture of THe OS: " +
                System.getProperty(architectureOS));
        System.out.println("userName: " +
                System.getProperty(userName));
        System.out.println("userHome: " +
                System.getProperty(userHome));

//        listFilesUsingJavaIO(System.getProperty(userHome)+"\\Documents").stream().forEach(e->{
//            System.out.println(e);
//        });

        System.out.println("userDir: " +
                System.getProperty(userDir));

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                byte[] hardwareAddress = ni.getHardwareAddress();
                if (hardwareAddress != null) {
                    String[] hexadecimalFormat = new String[hardwareAddress.length];
                    for (int i = 0; i < hardwareAddress.length; i++) {
                        hexadecimalFormat[i] = String.format("%02X", hardwareAddress[i]);
                    }
                    System.out.println(String.join("-", hexadecimalFormat));
                }
            }
//            InetAddress localHost = null;
//            try {
//                localHost = InetAddress.getLocalHost();
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
//            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
//            byte[] hardwareAddress = ni.getHardwareAddress();
//            String[] hexadecimal = new String[hardwareAddress.length];
//            for (int i = 0; i < hardwareAddress.length; i++) {
//                hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
//            }
//            String macAddress = String.join("-", hexadecimal);
//            System.out.println(macAddress);
            try {

                OutputStream output = new OutputStream() {
                    private StringBuilder string = new StringBuilder();

                    @Override
                    public void write(int b) throws IOException {
                        this.string.append((char) b );
                    }

                    //Netbeans IDE automatically overrides this toString()
                    public String toString() {
                        return this.string.toString();
                    }
                };
                Process proc = Runtime.getRuntime().exec("Rundll32.exe user32.dll,LockWorkStation");
//                byte[] buf = new byte[8192];
//                int length;
//                while ((length = proc.getInputStream().read(buf)) > 0) {
//                    output.write(buf, 0, length);
//                }
//
//                System.out.println(output);
            } catch (IOException e) {
                e.printStackTrace();
            }



        } catch (SocketException e) {
            e.printStackTrace();
        }


    }

    public static Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }



}


