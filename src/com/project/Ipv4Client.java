package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Ipv4Client {

    private final static int totalPackets = 12;
    private final static int headerSize = 20;

    public static void main(String[] args){
        Ipv4Client client = new Ipv4Client();

        client.main();
    }


    public void main(){
        try (Socket socket = new Socket("18.221.102.182", 38003)) {

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            byte[] packet;
            byte[] destAddress = socket.getInetAddress().getAddress();
            byte[] srcAddress = {127,0,0,1};

            for (int i=0; i < totalPackets; i++) {
                int dataSize = (int)java.lang.Math.pow(2,(i+1));
                int packetSize = headerSize + dataSize;
                packet = new byte[packetSize];

                packet[0] = 0x45; //version and hlen
                packet[1] = 0; //tos
                packet[2] = (byte)(packetSize>>>8); //size
                packet[3] = (byte)(packetSize);
                packet[4] = 0; //ident
                packet[5] = 0;
                packet[6] = 0x40; //flags
                packet[7] = 0; //offset
                packet[8] = 50; //ttl
                packet[9] = 6; //protocol
                packet[12] = srcAddress[0]; //source Address
                packet[13] = srcAddress[1];
                packet[14] = srcAddress[2];
                packet[15] = srcAddress[3];
                packet[16] = destAddress[0]; //destination Address
                packet[17] = destAddress[1];
                packet[18] = destAddress[2];
                packet[19] = destAddress[3];

                //calculate and replace checksum values
                short checksum = checksum(packet);

                packet[10] = (byte) (checksum >>> 8);
                packet[11] = (byte) checksum;

                //enter the data values as zero's
                for (int k=20; k<packet.length;k++){
                    packet[k] = 0;
                }

                //print packet number
                System.out.println("Packet length " + packet.length);

                //send packet
                os.write(packet);

                //print whether or not guess is correct
                System.out.println(br.readLine());

            }

        }catch(IOException e){
            System.out.println(e);
        }catch (Error e){
            System.out.println(e);
        }
    }

    public short checksum(byte[] b){
        int sum = 0;
        for (int i = 0; i < b.length; i+=2){
            //get first 2 halfs of values
            int firstHalf = b[i] << 8;
            firstHalf &= 0xFF00;
            int secondHalf = b[i+1] & 0xFF;

            sum += firstHalf + secondHalf;

            if ((sum & 0xFFFF0000) != 0){
                sum &= 0xFFFF;
                sum++;
            }
        }
        return (short)(~(sum & 0xFFFF));
    }


}
