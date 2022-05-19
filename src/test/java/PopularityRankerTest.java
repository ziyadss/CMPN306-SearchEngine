import com.cmpn306.database.WebGraph;
import com.cmpn306.database.WebGraphNode;
import com.cmpn306.ranker.PopularityRanker;
import com.cmpn306.ranker.Ranker;
import org.testng.annotations.Test;

import java.sql.SQLException;

class PopularityRankerTest {

    @Test
    static void pageRank() {
        Ranker.setTotalDocCount(5);
        WebGraph     webGraph      = new WebGraph();
        WebGraphNode webGraphNodea = new WebGraphNode("A", 1);
        WebGraphNode webGraphNodeb = new WebGraphNode("B", 1);
        WebGraphNode webGraphNodec = new WebGraphNode("C", 1);
        WebGraphNode webGraphNoded = new WebGraphNode("D", 1);
        WebGraphNode webGraphNodee = new WebGraphNode("E", 1);
        webGraph.getDocs().put("A", webGraphNodea);
        webGraph.getDocs().put("B", webGraphNodeb);
        webGraph.getDocs().put("C", webGraphNodec);
        webGraph.getDocs().put("D", webGraphNoded);
        webGraph.getDocs().put("E", webGraphNodee);
        webGraphNodea.getOutGoingUrls().put("B", webGraphNodeb);
        webGraphNodeb.getIncomingUrls().put("A", webGraphNodea);

        webGraphNodeb.getOutGoingUrls().put("E", webGraphNodee);
        webGraphNodee.getIncomingUrls().put("B", webGraphNodeb);

        webGraphNodec.getOutGoingUrls().put("A", webGraphNodea);
        webGraphNodea.getIncomingUrls().put("C", webGraphNodec);

        webGraphNodec.getOutGoingUrls().put("B", webGraphNodeb);
        webGraphNodeb.getIncomingUrls().put("C", webGraphNodec);

        webGraphNodec.getOutGoingUrls().put("D", webGraphNoded);
        webGraphNoded.getIncomingUrls().put("C", webGraphNodec);

        webGraphNoded.getOutGoingUrls().put("E", webGraphNodee);
        webGraphNodee.getIncomingUrls().put("D", webGraphNoded);

        webGraphNoded.getOutGoingUrls().put("C", webGraphNodec);
        webGraphNodec.getIncomingUrls().put("D", webGraphNoded);

        webGraphNodee.getOutGoingUrls().put("D", webGraphNoded);
        webGraphNoded.getIncomingUrls().put("E", webGraphNodee);

        PopularityRanker popularityRanker = new PopularityRanker();
        popularityRanker.setWebGraph(webGraph);
        webGraphNodea.setPageRank(1.0);
        webGraphNodeb.setPageRank(1.0);
        webGraphNodec.setPageRank(1.0);
        webGraphNoded.setPageRank(1.0);
        webGraphNodee.setPageRank(1.0);
        int iterCount = 0;
        System.out.println("===============================");
        while (popularityRanker.notConverged(iterCount)) {
            System.out.println(webGraphNodea.getPageRank());
            System.out.println(webGraphNodeb.getPageRank());
            System.out.println(webGraphNodec.getPageRank());
            System.out.println(webGraphNoded.getPageRank());
            System.out.println(webGraphNodee.getPageRank());
            System.out.println("===============================");
            popularityRanker.calculatePageRank();
            iterCount++;

        }
        System.out.println("===============================");
        System.out.println(webGraphNodea.getPageRank());
        System.out.println(webGraphNodeb.getPageRank());
        System.out.println(webGraphNodec.getPageRank());
        System.out.println(webGraphNoded.getPageRank());
        System.out.println(webGraphNodee.getPageRank());
        System.out.println("===============================");

    }

    public static void main(String[] args) throws SQLException {
        pageRank();
    }
}