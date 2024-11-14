package udp;

import java.net.*; 

class UDPServer { 
  public static void main(String args[]) throws Exception 
  { 
    int port = 0;
    int maxSeq = 0;
    if(args.length != 2){
      System.out.println("USAGE: UDPClient maxSeq port");
      System.exit(1);
    }
    try{
      maxSeq = Integer.parseInt(args[0]);//get startup info from args
      port = Integer.parseInt(args[1]);
    }
    catch(Error e){
      System.out.println("Bad input");
      System.exit(1);
    }
    DatagramSocket serverSocket = new DatagramSocket(port); 
    int Seq = 0;
    int minusOne;
    String reply = "";
    while(true) //wait for client to send input
    { 
      boolean corrupted = false;
      byte[] receiveData = new byte[1024]; 
      byte[] sendData; 
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
      serverSocket.receive(receivePacket); 
      String sentence = new String(receivePacket.getData());
      String [] inArr = sentence.split(" ",3);
      if(inArr.length != 3){
        corrupted = true;
      }
      if(!corrupted){
        if(inArr[0].equals("DATA")){
          try {
              if(Integer.parseInt(inArr[1]) == Seq){
                System.out.println("FROM CLIENT: " + inArr[2]);//print the char that was sent
                reply = "ACK " + Seq;//respond with ack
                if(Seq >= maxSeq){
                  Seq=0;//reset the seq if max seq is reached
                }
                else{
                  Seq++;
                }
              }
              else{
                corrupted = true;
              }
          } catch (NumberFormatException e) {
            corrupted = true;
          }
        }
      }
      if(corrupted){
        minusOne = Seq - 1;//if corrupted set minus one
        reply = "ACK " + minusOne;// respond with the ack for the last char recieved
      }
      InetAddress IPAddress = receivePacket.getAddress(); 
      int returnPort = receivePacket.getPort(); 
      sendData = reply.getBytes(); 
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, returnPort); 
      serverSocket.send(sendPacket); //send response
    } 
  } 
}  

