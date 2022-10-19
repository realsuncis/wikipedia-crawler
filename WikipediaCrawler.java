/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikipediaCrawler
{
    static long visited = 0;
    static boolean runSearch = true;
    static String destinationURL;
    private static String[] items = {"Main_Page", ":"};

    public static void main(String[] args) throws InterruptedException, IOException
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Start URL:");
        String startURL = scanner.nextLine();
        System.out.println("Destination URL:");
        destinationURL = scanner.nextLine();
        scanner.close();
        LinkedList<Node> urlQueue = new LinkedList<>();
        urlQueue.add(new Node(startURL));
        HashSet<String> visitedNodes = new HashSet<>();
        long time1 = System.nanoTime();

        Node urlNode = null;
        String pattern = "(?<=<a href=\"\\/wiki\\/)(.*?)(?=\")";
        Pattern r = Pattern.compile(pattern);

        outerloop:
        while((urlNode = urlQueue.poll()) != null && runSearch)
        {
            URL URLConnection = null;
            try 
            {
                URLConnection = new URL(urlNode.url);
            } 
            catch (MalformedURLException e) 
            {
                continue;
            }

            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(URLConnection.openStream()));
            String line = null;
            while((line = in.readLine()) != null)
            {
                Matcher m = r.matcher(line);
                while(m.find()) 
                {
                    String link = m.group(0);
                    if(stringContainsItemFromList(link)) continue;
                    if(visitedNodes.contains(link)) continue;
                    visited++;
                    if(("https://en.wikipedia.org/wiki/" + link).equals(destinationURL))
                    {
                        Node finalNode = new Node(destinationURL);
                        finalNode.parent = urlNode;
                        Node tempNode = finalNode;
                        Stack<Node> reversePath = new Stack<>();
                        while(tempNode!=null)
                        {
                            reversePath.add(tempNode);
                            tempNode = tempNode.parent;
                        }

                        while(!reversePath.isEmpty())
                        {
                            System.out.print(reversePath.pop().url);
                            if(!reversePath.isEmpty())
                            {
                                System.out.print(" -> ");
                            }
                        }
                        break outerloop;
                    }
                    else
                    {
                        Node newNode = new Node("https://en.wikipedia.org/wiki/" + link);
                        newNode.parent = urlNode;
                        urlQueue.add(newNode);
                    }
                }
                if(!visitedNodes.contains(urlNode.url)) visitedNodes.add(urlNode.url);
            }
            
        }
        System.out.print("\nTime:"+(System.nanoTime()-time1)/1000000000);
        System.out.println("\nVisited links:" + visited);

    }
    
    public static boolean stringContainsItemFromList(String inputStr)
    {
        for(int i =0; i < items.length; i++)
        {
            if(inputStr.contains(items[i]))
            {
                return true;
            }
        }
        return false;
    }
}

class Node
{
    public Node parent;
    public String url;

    Node(String url)
    {
            this.url = url;
    }
}