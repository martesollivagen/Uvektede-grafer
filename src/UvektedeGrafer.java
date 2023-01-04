import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class UvektedeGrafer {

    public static void main(String[] args) {
        Graf graf = new Graf();
        graf.nyUgraf( "src/ø6g4");
        graf.finnSamKomponenter();
    }
}

class Kant {
    Kant neste;
    Node til;

    public Kant(Node n, Kant neste){
        til = n;
        this.neste = neste;
    }

}

class Node {
    Kant kant1;
    int antallKanter;
    int ferdig_tid;
    int index;
    Object d;

    public Node(){
        kant1 = null;
    }

    public int getFerdig_tid() {
        return ferdig_tid;
    }

    public void setFerdig_tid(int ferdig_tid) {
        this.ferdig_tid = ferdig_tid;
    }
}

class Graf{
    int N, K;
    Node[] node;

    public void nyUgraf(String filepath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
            StringTokenizer st = new StringTokenizer(br.readLine());
            N = Integer.parseInt(st.nextToken());
            node = new Node[N];
            for (int i = 0; i<N; i++){
                node[i] = new Node();
                node[i].index = i;
            }
            K = Integer.parseInt(st.nextToken());
            for (int i = 0; i<K; i++){
                st = new StringTokenizer(br.readLine());
                int fra = Integer.parseInt(st.nextToken());
                int til = Integer.parseInt(st.nextToken());
                Kant k = new Kant(node[til], node[fra].kant1);
                node[fra].antallKanter++;
                node[fra].kant1 = k;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void dfs_init(){
        for (int i = N; i-->0;){
            node[i].d = new Dfs_forgj();
        }
        Dfs_forgj.null_tid();
    }

    public void df_sok(Node n){
        Dfs_forgj nd = (Dfs_forgj) n.d;
        nd.funnet_tid = Dfs_forgj.les_tid();
        for(Kant k = n.kant1; k!=null; k=k.neste){
            Dfs_forgj md = (Dfs_forgj) k.til.d;
            if (md.funnet_tid == 0){
                md.forgj = n;
                md.distanse = nd.distanse +1;
                df_sok(k.til);
            }
        }
        nd.ferdig_tid = Dfs_forgj.les_tid();
        n.setFerdig_tid(nd.ferdig_tid);
    }

    public void dfs(Node s){
        dfs_init();
        ((Dfs_forgj)s.d).distanse = 0;
        df_sok(s);
    }

    public void setFerdigTid(){
        dfs(node[0]);
        for(Node n: node){
            if (n.ferdig_tid == 0){
                df_sok(n);
            }
        }
    }

    private int fintIndex(Node n){
        if (n == null) return -1;
        return List.of(node).indexOf(n);
    }

    public void transpose(){
        Node[] liste = new Node[N];

        for (int i = 0; i<N; i++){
            liste[i] = new Node();
            liste[i].index = node[i].index;
        }

        for (int i = 0; i<N; i++) {
            for (int j = 0; j < node[i].antallKanter; j++) {
                int fra = fintIndex(node[i].kant1.til);
                Kant k = new Kant(liste[i], liste[fra].kant1);
                liste[fra].kant1 = k;
                liste[fra].antallKanter++;
                node[i].kant1 = node[i].kant1.neste;
            }
        }
        this.node = liste;
    }

    public void finnSamKomponenter(){
        setFerdigTid();
        Arrays.sort(node, Comparator.comparingInt(Node::getFerdig_tid).reversed());
        transpose();
        printSamKomponenter();
    }

    public void printKanter(){
        Node[] liste = new Node[N];

        for (int i = 0; i<N; i++){
            liste[i] = new Node();
            liste[i].index = node[i].index;
            liste[i].antallKanter = node[i].antallKanter;

        }
        for(int i = 0; i<N; i++){
            if(node[i].antallKanter == 0) {
                System.out.println("Index " + liste[i].index + " har ingen kanter");
            }else {
                StringBuilder sb = new StringBuilder("Index " + liste[i].index + " har kant til:   ");
                Node n = node[i];
                for (int j = 0; j<node[i].antallKanter; j++){
                    sb.append(n.kant1.til.index).append(", ");
                    Kant k = new Kant(liste[fintIndex(n.kant1.til)], liste[i].kant1);
                    liste[i].kant1 = k;
                    n.kant1 = n.kant1.neste;
                }
                System.out.println(sb);
            }
        }
        this.node = liste;
    }

    public void printSamKomponenter(){
        StringBuilder sb = new StringBuilder().append("\nKomponent    Noder i komponenten");
        int i = 0;
        int tepmtid = 0;

        dfs_init();

        for(Node n: node){
            if (n.ferdig_tid == 0){
                i++;
                sb.append("\n").append(i).append("            ");
                df_sok(n);

                for(Node j: node){
                    if (j.ferdig_tid>tepmtid){
                        sb.append(j.index).append("  ");
                    }
                }
                tepmtid = n.ferdig_tid;
            }
        }
        System.out.println(sb);
    }


}

class Forgj {
    int distanse;
    Node forgj;
    static int uendelig = 1000000000;

    public Forgj(){
        distanse = uendelig;
    }

    public int finn_dist(){
        return distanse;
    }

    public Node finnForgj(){
        return forgj;
    }


}

class Dfs_forgj extends Forgj {
    int funnet_tid, ferdig_tid;
    static int tid;

    static void null_tid(){
        tid = 0;
    }

    static int les_tid(){
        return ++tid;
    }

    public int getFunnet(){
        return funnet_tid;
    }

}