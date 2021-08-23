package sec03.brd08;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

/**
 * Servlet implementation class BoardController
 */
@SuppressWarnings("serial")
@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	
	BoardService boardService;
	ArticleVO articleVO;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
		doHandle(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String nextPage = "";
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		HttpSession session;	// 세션 선언
		
		String action = request.getPathInfo();
		System.out.println("action:" + action);
		
		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			
			// 기본 컨트롤러
			if (action==null){														// board로만 호출했을때
				String _section=request.getParameter("section");
				String _pageNum=request.getParameter("pageNum");
				
				int section = Integer.parseInt(((_section==null)? "1":_section));	// null이면 1로 초기화
				int pageNum = Integer.parseInt(((_pageNum==null)? "1":_pageNum));	// null이면 1로 초기화
				
				Map<String, Integer> pagingMap = new HashMap<String, Integer>();
				
				pagingMap.put("section", section);									// section 맵에 담기 
				pagingMap.put("pageNum", pageNum);									// pageNum 맵에 담기 
				
				Map articlesMap=boardService.listArticles(pagingMap);				// 전체게시글 페이징 갯수에맞게 가져오기
				
				articlesMap.put("section", section);								// 맵에 추가로 section 담기
				articlesMap.put("pageNum", pageNum);								// 맵에 추가로 pageNum 담기
				
				request.setAttribute("articlesMap", articlesMap);					// Request에 articlesMap 바인딩
				nextPage = "/board07/ listArticles.jsp";							// view 페이지 지정
				
			// 게시글 리스트 컨트롤러	
			}else if(action.equals("/listArticles.do")){
				
				String _section=request.getParameter("section");
				String _pageNum=request.getParameter("pageNum");
				
				int section = Integer.parseInt(((_section==null)? "1":_section));	// null이면 1로 초기화
				int pageNum = Integer.parseInt(((_pageNum==null)? "1":_pageNum));	// null이면 1로 초기화
				
				Map pagingMap=new HashMap();
				pagingMap.put("section", section);									// section 맵에 담기
				pagingMap.put("pageNum", pageNum);									// pageNum 맵에 담기 
				
				Map articlesMap=boardService.listArticles(pagingMap);				// 전체게시글 페이징 갯수에맞게 가져오기
				articlesMap.put("section", section);								// 맵에 추가로 section 담기
				articlesMap.put("pageNum", pageNum);								// 맵에 추가로 pageNum 담기
				
				request.setAttribute("articlesMap", articlesMap);					// Request에 articlesMap 바인딩
				nextPage = "/board07/listArticles.jsp";								// view 페이지 지정
				
			// 게시글 폼 컨트롤러	
			} else if (action.equals("/articleForm.do")) {
				nextPage = "/board07/articleForm.jsp";								// view 페이지 지정
			
			// 게시글 생성 컨트롤러
			} else if (action.equals("/addArticle.do")) {
				
				int articleNO = 0;
				
				Map<String, String> articleMap = upload(request, response);			// 업로드?
				
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");

				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				
				articleNO = boardService.addArticle(articleVO);
				
				
				if (imageFileName != null && imageFileName.length() != 0) {
					
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('새글을 추가했습니다..');" + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");

				return;
				
			// 답글 게시글 컨트롤러	
			} else if (action.equals("/viewArticle.do")) {
				
				String articleNO = request.getParameter("articleNO");
				
				articleVO = boardService.viewArticle(Integer.parseInt(articleNO));
				
				request.setAttribute("article", articleVO);
				
				nextPage = "/board07/viewArticle.jsp";
			
			// 	
			} else if (action.equals("/modArticle.do")) {
				
				Map<String, String> articleMap = upload(request, response);
				
				int articleNO = Integer.parseInt(articleMap.get("articleNO"));
				
				articleVO.setArticleNO(articleNO);
				
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				
				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				
				boardService.modArticle(articleVO);
				
				if (imageFileName != null && imageFileName.length() != 0) {
					
					String originalFileName = articleMap.get("originalFileName");
					
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					
					File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + originalFileName);
					oldFile.delete();
				}
				
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('���� �����߽��ϴ�.');" + " location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO=" + articleNO + "';" + "</script>");
				return;
			} else if (action.equals("/removeArticle.do")) {
				
				int articleNO = Integer.parseInt(request.getParameter("articleNO"));
				
				List<Integer> articleNOList = boardService.removeArticle(articleNO);
				
				for (int _articleNO : articleNOList) {
					
					File imgDir = new File(ARTICLE_IMAGE_REPO + "\\" + _articleNO);
					
					if (imgDir.exists()) {
						FileUtils.deleteDirectory(imgDir);
					}
				}

				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('���� �����߽��ϴ�.');" + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");
				return;

			} else if (action.equals("/replyForm.do")) {		// 답글쓰기
				
				int parentNO = Integer.parseInt(request.getParameter("parentNO"));	// 글번호
				
				session = request.getSession();					// 세션 객체 생성(있으면 가져오고)
				session.setAttribute("parentNO", parentNO);
				
				nextPage = "/board06/replyForm.jsp";
				
			} else if (action.equals("/addReply.do")) {
				
				session = request.getSession();
				int parentNO = (Integer) session.getAttribute("parentNO");
				
				session.removeAttribute("parentNO");
				
				Map<String, String> articleMap = upload(request, response);
				
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				
				articleVO.setParentNO(parentNO);
				articleVO.setId("lee");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				
				int articleNO = boardService.addReply(articleVO);
				
				if (imageFileName != null && imageFileName.length() != 0) {
					
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('����� �߰��߽��ϴ�.');" + " location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO="+articleNO+"';" + "</script>");
				return;
			
			}else {
				nextPage = "/board06/listArticles.jsp";
			}

			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Map<String, String> articleMap = new HashMap<String, String>();
		
		String encoding = "utf-8";
		
		File currentDirPath = new File(ARTICLE_IMAGE_REPO);
		
		DiskFileItemFactory factory = new DiskFileItemFactory();	// 업로드 관련 API 객체 생성 (1)
		factory.setRepository(currentDirPath);						// 업로드할 경로 설정
		factory.setSizeThreshold(1024 * 1024);						// 파일 크기 설정
		
		ServletFileUpload upload = new ServletFileUpload(factory);	// 업로드 관련 API 객체 생성 (2)
		
		try {
			
			List items = upload.parseRequest(request);				// 전달받은 매개변수르 List로 받는다
			
			for (int i = 0; i < items.size(); i++) {
				
				FileItem fileItem = (FileItem) items.get(i);		// 파일 업로드 창에서 업로드된 항목들을 하나씩 가져온다
				
				// form 데이터이면 true, 파일데이터이면 false
				// 일반 파라미터이면 true, 아니면(파일이면) false
				if (fileItem.isFormField()) {
					
					System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
					
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
					
				} else {
					
					System.out.println("매개변수이름:" + fileItem.getFieldName());
					System.out.println("파일이름:" + fileItem.getName());
					System.out.println("파일크기:" + fileItem.getSize() + "bytes");
					//articleMap.put(fileItem.getFieldName(), fileItem.getName());
					
					if (fileItem.getSize() > 0) {
						int idx = fileItem.getName().lastIndexOf("\\");
						
						if (idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");
						}

						String fileName = fileItem.getName().substring(idx + 1);
						System.out.println("���ϸ�:" + fileName);
						
						articleMap.put(fileItem.getFieldName(), fileName);  //�ͽ��÷η����� ���ε� ������ ��� ���� �� map�� ���ϸ� ����);
						
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						
						fileItem.write(uploadFile);

					} // end if
				} // end if
			} // end for
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleMap;
	}

}
