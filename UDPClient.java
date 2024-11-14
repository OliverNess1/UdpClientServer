package udp;

import java.io.*; 
import java.net.*; 
  
class UDPClient { 
  public static void main(String args[]) throws Exception 
  {
    int maxSeq = 0;
    int port = 0;
    if(args.length != 2){
      System.out.println("USAGE: UDPClient maxSeq port");
      System.exit(1);
    }
    try{
      maxSeq = Integer.parseInt(args[0]);  //get maximum sequence and port # from args
      port = Integer.parseInt(args[1]);
    }
    catch(Error e){
      System.out.println("Bad input");
      System.exit(1);
    }
    String hostName = "net01.utdallas.edu"; //hardcoded for ease of use
    int Seq = 0;
    boolean corrupted;

      
    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
    DatagramSocket clientSocket = new DatagramSocket(); 
    InetAddress IPAddress = InetAddress.getByName(hostName); // start the connection
    String toSend; 
    byte[] sendData; 
    byte[] receiveData = new byte[1024]; 
    String sentence;
    while ((sentence = inFromUser.readLine()) != null) { //keep sending as long as user keeeps inputtting
      for(int j = 0; j < sentence.length(); j++){// send the string as a sequence of chars
        corrupted = false;
        toSend = "DATA " + Seq + " " + sentence.charAt(j);//format output
        sendData = toSend.getBytes();
        System.out.println(toSend); //so we can check the output
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); //send data
        clientSocket.send(sendPacket); 
        try {
          clientSocket.setSoTimeout(5000); //timeout in case of failure
          } 
          catch (SocketException e) {
            System.out.println("Error setting timeout" + e);
          }
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
          clientSocket.receive(receivePacket);//get the response
          String modifiedSentence = new String(receivePacket.getData()); 
          String[] inArr = modifiedSentence.split(" "); //split it on spaces
          if(inArr.length == 2){
            if(inArr[0].equals("ACK")){//make sure formatting is right
              try { 
                if(Integer.parseInt(inArr[1].trim()) == Seq){ //make sure seq is right
                  System.out.println(modifiedSentence);//print the response
                  if(Seq >= maxSeq){//reset the seq if maxseq is reached
                    Seq=0;
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
            else{
              corrupted = true;
            }
          }
          else{
            corrupted = true;
          }
          if(corrupted){// if the response is bad then we decrement the seq so we send the same packet again
            j--;
          }
          } catch (SocketTimeoutException e) {
          j--;
          }    
      }         
      
    }
    clientSocket.close(); 
  } 
} 

