package sec03.brd02;

import java.io.File;
import java.io.IOException;
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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class BoardController
 */
//@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	
	// 업로드 경로
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	BoardService boardService;
	ArticleVO articleVO;


	public void init(ServletConfig config) throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHandle(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String nextPage = "";
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		String action = request.getPathInfo();	// board/" " 요청 경로 얻기
		System.out.println("action:" + action);
		
		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			
			if (action == null) {
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board02/listArticles.jsp";
			
			} else if (action.equals("/listArticles.do")) {
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board02/listArticles.jsp";
			
			} else if (action.equals("/articleForm.do")) {
				nextPage = "/board02/articleForm.jsp";
			
			} else if (action.equals("/addArticle.do")) {
				
				// 업로드 메소드 실행, Map으로 리턴받는다
				Map<String, String> articleMap = upload(request, response); 
				// 각 결과값을 꺼내서 담는다
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");

				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				boardService.addArticle(articleVO);			// 글 추가
				nextPage = "/board/listArticles.do";
			
			}else {
				nextPage = "/board02/listArticles.jsp";
			}

			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);			// 포워딩
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 업로드
	@SuppressWarnings("rawtypes")
	private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Map<String, String> articleMap = new HashMap<String, String>();
		
		String encoding = "utf-8";									// 인코딩 타입
		
		File currentDirPath = new File(ARTICLE_IMAGE_REPO);			// 업로드 경로
		
		DiskFileItemFactory factory = new DiskFileItemFactory(); 	// 업로드 관련 API 객체 생성 (1)
		
		factory.setRepository(currentDirPath);						// 업로드 경로
		factory.setSizeThreshold(1024 * 1024);						// 파일 최대 크기
		
		ServletFileUpload upload = new ServletFileUpload(factory);	// 업로드 관련 API 객체 생성 (2)
		
		try {
			List items = upload.parseRequest(request);				// 전달받은 매개변수르 List로 받는다
			
			for (int i = 0; i < items.size(); i++) {
				
				FileItem fileItem = (FileItem) items.get(i);
				
				if (fileItem.isFormField()) {
					
					System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
					
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
					
				} else {
					
					System.out.println("파라미터 이름:" + fileItem.getFieldName());
					System.out.println("파일 이름:" + fileItem.getName());
					System.out.println("파일 크기:" + fileItem.getSize() + "bytes");
					
					//articleMap.put(fileItem.getFieldName(), fileItem.getName());
					if (fileItem.getSize() > 0) {
						
						int idx = fileItem.getName().lastIndexOf("\\");
						if (idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");
						}

						String fileName = fileItem.getName().substring(idx + 1);
						System.out.println("파일 이름:" + fileName);
						
						articleMap.put(fileItem.getFieldName(), fileName);  
						
						//�ͽ��÷η����� ���ε� ������ ��� ���� �� map�� ���ϸ� ����
						
						File uploadFile = new File(currentDirPath + "\\" + fileName);
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
