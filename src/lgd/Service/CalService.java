package lgd.Service;

import java.io.*;
import java.util.*;

/**
 * @author lgd
 * @date 2022/7/24 10:27
 */
public class CalService {

    class Node{
        String id;
        double ts;

        public Node(String id, double ts) {
            this.id = id;
            this.ts = ts;
        }
    }

    public static void calTwoFile(File file1, File file2) throws IOException {
        int time=1,cnt1=0,cnt2=0,same=0;
        double sumTs=0;
        Scanner sc1=new Scanner(file1);
        Scanner sc2=new Scanner(file2);
        Set<String> set=new HashSet<>();
        Map<String,Double> src=new HashMap<>();
        Map<String,Double> des=new HashMap<>();
        FileOutputStream out=new FileOutputStream("E:\\工具\\5g测量\\周报\\724\\cal.txt");
        while (sc1.hasNext() || sc2.hasNext()){
            double tempTime=time-1;
            String srcLasId="";
            double srcLastTs=0;
            while (sc1.hasNext()&&tempTime<=time){
                String s = sc1.nextLine();
                StringTokenizer tokenizer=new StringTokenizer(s);
                while (tokenizer.hasMoreElements()){
                    String id = tokenizer.nextToken();
                    tempTime=Double.parseDouble(tokenizer.nextToken());
                    if(tempTime<=time){
                        set.add(id);
                        src.put(id,tempTime);
                    }else{
                        srcLasId=id;
                        srcLastTs=tempTime;
                    }
                }
            }
            tempTime=time-1;
            while (sc2.hasNext() && tempTime<=time){
                String s=sc2.nextLine();
                StringTokenizer tokenizer=new StringTokenizer(s);
                while (tokenizer.hasMoreElements()){
                    String id = tokenizer.nextToken();
                    tempTime=Double.parseDouble(tokenizer.nextToken());
                    if(set.contains(id)){
                        same++;
                        sumTs+=tempTime-src.get(id);
                    }else
                        cnt2++;
                }

            }
            System.out.println(sumTs);
            StringBuilder sb=new StringBuilder();
            sb.append(time-1).append("~").append(time).append("        ").append(sumTs/same).append("         ").append(same).append("\n");
            out.write(sb.toString().getBytes());
            time++;
            same=0;
            sumTs=0;
            src.clear();
            set.clear();
            set.add(srcLasId);
            src.put(srcLasId,srcLastTs);
        }
    }
}
