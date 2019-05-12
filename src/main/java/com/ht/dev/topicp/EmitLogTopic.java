package com.ht.dev.topicP;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;

public class EmitLogTopic {

  private static final String EXCHANGE_NAME = "topic_logs";

  public static void main(String[] argv)
  {
    Connection connection = null;
    Channel channel = null;
    try
    {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");

      connection = factory.newConnection();
      channel = connection.createChannel();

      //Declarer un Exchange de type TOPIC
      channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

      // * (Étoile) peut remplacer exactement un mot.
      // # (Hash) peut remplacer entre zéro ou plusieurs mots.
      // un message est constitué de mots séparés par des points
      // anapath.production.pdf  
      String routingKey = getRouting(argv); //recuperer la clé de routage
      String message = getMessage(argv); //et le message

      //envoyer le message sans propriétés
      channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
      System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

    }
    catch  (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (connection != null) {
        try {
          connection.close();
        }
        catch (IOException ignore) {}
      }
    }
  }

  //recupere le parametre 0 de la ligne des arguments
  //comme clés de routage...
  private static String getRouting(String[] strings){
    if (strings.length < 1)
    	    return "anonymous.info";
    return strings[0];
  }

  //les arguments du numero 1 a ++ feront partie du message
  private static String getMessage(String[] strings){
    if (strings.length < 2)
    	    return "Bonjour le monde!";
    return joinStrings(strings, " ", 1);
  }

  private static String joinStrings(String[] strings, String delimiter, int startIndex) {
    int length = strings.length;
    if (length == 0 ) return "";
    if (length < startIndex ) return "";
    StringBuilder words = new StringBuilder(strings[startIndex]);
    for (int i = startIndex + 1; i < length; i++) {
        words.append(delimiter).append(strings[i]);
    }
    return words.toString();
  }
}

