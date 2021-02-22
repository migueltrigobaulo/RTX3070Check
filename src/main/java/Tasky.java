import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tasky extends TimerTask {

    private ArrayList<String> inStockNotified = new ArrayList();
    private String name;
    private MyBot myBot;

    public Tasky(String n, MyBot myBot) {
        this.name = n;
        this.myBot = myBot;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " " + name + " the task has executed successfully " + new Date());
        try {
            download("https://www.pccomponentes.com/tarjetas-graficas/geforce-rtx-3070-series");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File("geforce-rtx-3070-series");
        ArrayList<String> matchedItems;
        ArrayList<String> resultItems;
        if (file.exists()) {
            Pattern pattern = Pattern.compile("<article.*?</article>");
            try {
                String fileContent = Files.readString(Path.of(file.getAbsolutePath()));
                matchedItems = new ArrayList<>(getMatchingStrings(fileContent, pattern));

                for (int matchedItem = 0; matchedItem < matchedItems.size(); matchedItem++) {
                    String name = getMatchingStrings(matchedItems.get(matchedItem), Pattern.compile("<article.*?data-id")).get(0);
                    name = name.substring(20, name.length() - 9);
                    String value = getMatchingStrings(matchedItems.get(matchedItem), Pattern.compile("data-price.*?' ")).get(0);
                    value = value.substring(12, value.length() - 2);
                    String avaliability = getMatchingStrings((matchedItems.get(matchedItem)), Pattern.compile("data-stock-web.*?' ")).get(0);
                    avaliability = avaliability.substring(16, avaliability.length() - 2);
                    Boolean isAvaliable = Integer.parseInt(avaliability) == 1;
                    String link = getMatchingStrings((matchedItems.get(matchedItem)), Pattern.compile("href.*?\" ")).get(0);
                    link = "www.pccomponentes.com" + link.substring(6, link.length() - 2);
                    if (isAvaliable && !inStockNotified.contains(name)) {
                        myBot.sendMessageToAll(name + " está disponible a " + value + "€\n " + link + "\n");
                        inStockNotified.add(name);
                    } else {
                        if (inStockNotified.contains(name)){
                            inStockNotified.remove(name);
                            myBot.sendMessageToAll("Nombre: " + name + " se ha agotado! :(\n");
                        }
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if ("Tasky".equalsIgnoreCase(name)) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void download(String urlString) throws IOException {
        URL url = new URL(urlString);
        try(
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter("geforce-rtx-3070-series"));
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
            }
            System.out.println("Page downloaded.");
        }
    }

    static List<String> getMatchingStrings(String string, Pattern pattern) {
        Matcher matcher = pattern.matcher(string);
        ArrayList<String> matchedItems = new ArrayList<>();
        while (matcher.find()) {
            String match = matcher.group();
            matchedItems.add(match);
        }
        return matchedItems;
    }
}