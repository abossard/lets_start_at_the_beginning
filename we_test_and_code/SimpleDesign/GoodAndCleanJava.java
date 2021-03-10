package fitnesse.html;
import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class SetupTeardownIncluder {

    /*
    Usage:
    If I need the test page:
    
    bool isSuite = true;
    PageData myPageData = <from somewhere>

    String output = SetupTeardownIncluder.render(myPageData, isSuite);
    
    */
    public static String render(PageData pageData, boolean isSuite) throws Exception {
        if (pageData.hasAttribute("Test")) {
            var testPage = pageData.getWikiPage();
            var newPageContent = new StringBuffer();
            if(isSuite) {
                newPageContent.append(makeIncludeStatement(SuiteResponder.SUITE_SETUP_NAME, "-setup"))
            }
            newPageContent.append(makeIncludeStatement("SetUp", "-setup");
            newPageContent.append(pageData.getContent());
            newPageContent.append(makeIncludeStatement("TearDown", "-teardown");
            if(isSuite) {
                newPageContent.append(makeIncludeStatement(SuiteResponder.SUITE_TEARDOWN_NAME, "-teardown");
            }
            return new PageData(newPageContent.toString()).getHtml();
        }
        return pageData.getHtml();
    }

    private static String makeIncludeStatement(
            WikiPage forPage
            String withPageName, 
            String arg
        ) throws Exception {
            WikiPage inheritedPage = PageCrawlerImpl.getInheritedPage(withPageName, forPage);
            if (inheritedPage != null) {
                string pagePathName = PathParser.render(testPage.getPageCrawler().getFullPath(page));
                 return "\n!include " + arg + " ." + pagePathName + "\n";
        }
    }
}