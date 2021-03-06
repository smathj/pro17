package sec03.brd08;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardService {
	BoardDAO boardDAO;

	public BoardService() {
		boardDAO = new BoardDAO();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map listArticles(Map<String, Integer> pagingMap) {
		
		Map articlesMap = new HashMap();
		
		List<ArticleVO> articlesList = boardDAO.selectAllArticles(pagingMap);
		
		int totArticles = boardDAO.selectTotArticles();
		
		articlesMap.put("articlesList", articlesList);		// articlesList.articlesList에 게시글 들어있다
		articlesMap.put("totArticles", totArticles);
		
		//articlesMap.put("totArticles", 170);
		return articlesMap;
	}

	@SuppressWarnings("unchecked")
	public List<ArticleVO> listArticles() {
		List<ArticleVO> articlesList = boardDAO.selectAllArticles();
		return articlesList;
	}

	public int addArticle(ArticleVO article) {
		return boardDAO.insertNewArticle(article);
	}

	public ArticleVO viewArticle(int articleNO) {
		ArticleVO article = null;
		article = boardDAO.selectArticle(articleNO);
		return article;
	}

	public void modArticle(ArticleVO article) {
		boardDAO.updateArticle(article);
	}

	public List<Integer> removeArticle(int articleNO) {
		List<Integer> articleNOList = boardDAO.selectRemovedArticles(articleNO);
		boardDAO.deleteArticle(articleNO);
		return articleNOList;
	}

	public int addReply(ArticleVO article) {
		return boardDAO.insertNewArticle(article);
	}

}
