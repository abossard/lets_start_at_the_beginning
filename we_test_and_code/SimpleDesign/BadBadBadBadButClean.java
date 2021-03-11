package fitnesse.html;
import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

// STRAIGHT FROM THE CLEAN CODE BOOK!!!!!

// What is this guy doing?
public class SetupTeardownIncluder {
    private PageData pageData;
    private boolean isSuite;
    private WikiPage testPage;
    private StringBuffer newPageContent;
    private PageCrawler pageCrawler;

    // a method because Java has no default parameter, fine
    // (Later we'll learn even that is useless here)
    public static String render(PageData pageData) throws Exception {
        return render(pageData, false);
    }

    // A static method that creates an instance and call the non-static method
    // That's completely useless, as e.g. the instance is not reused
    public static String render(PageData pageData, boolean isSuite) throws Exception {
        return new SetupTeardownIncluder(pageData).render(isSuite);
    }

    // private constructor will ALWAYS call this methods.
    // Keep that in mind, important for later.
    private SetupTeardownIncluder(PageData pageData) {
        this.pageData = pageData;
        this.testPage = pageData.getWikiPage();
        this.pageCrawler = testPage.getPageCrawler();
        this.newPageContent = new StringBuffer();
    }

    // Now it get interesting, only if isSuite==true
    private String render(boolean isSuite) throws Exception {
        this.isSuite = isSuite;
        if (isTestPage()) includeSetupAndTeardownPages();
        // here we learn, if it's NOT a isTestPage, then NONE of the private fields is accessed! 
        // What a waste of cognitive load
        return pageData.getHtml();
    }

    // adds to the cognitive load, without providing a value
    private boolean isTestPage() throws Exception {
        return pageData.hasAttribute("Test");
    }

    // hides what it's doing, it's 100% sequential but no easy to test
    private void includeSetupAndTeardownPages() throws Exception {
        includeSetupPages();
        includePageContent();
        includeTeardownPages();
        updatePageContent();
    }

    // deeply hidden use of the isSuite flag from the beginning, but still inclear what actual steps it influences
    private void includeSetupPages() throws Exception {
        if (isSuite) includeSuiteSetupPage();
        includeSetupPage();
    }

    // include is already a shortcut! this just calls include with parameters
    private void includeSuiteSetupPage() throws Exception {
        include(SuiteResponder.SUITE_SETUP_NAME, "-setup");
    }
    private void includeSetupPage() throws Exception {
        include("SetUp", "-setup");
    }

    // this is poison: It actually access two private fields, changes one, and hides that
    private void includePageContent() throws Exception {
        newPageContent.append(pageData.getContent());
    }

    // again, isSuite is used, without specifying what it means
    private void includeTeardownPages() throws Exception {
        includeTeardownPage();
        if (isSuite) includeSuiteTeardownPage();
    }

    // private shortcut
    private void includeTeardownPage() throws Exception {
        include("TearDown", "-teardown");
    }
    private void includeSuiteTeardownPage() throws Exception {
        include(SuiteResponder.SUITE_TEARDOWN_NAME, "-teardown");
    }

    // WHAT, changes the passed down pageData object! In deeply nested method call.
    // Quite a surprise for whoever would have used a "render"er....
    // Would you expect a "render"-Method to change the page you give it to render?
    private void updatePageContent() throws Exception {
        pageData.setContent(newPageContent.toString());
    }

    
    private void include(String pageName, String arg) throws Exception {
        WikiPage inheritedPage = findInheritedPage(pageName);
        if (inheritedPage != null) {
            String pagePathName = getPathNameForPage(inheritedPage);
            buildIncludeDirective(pagePathName, arg);
        }
    }
    private WikiPage findInheritedPage(String pageName) throws Exception {
        return PageCrawlerImpl.getInheritedPage(pageName, testPage);
    }
    private String getPathNameForPage(WikiPage page) throws Exception {
        WikiPagePath pagePath = pageCrawler.getFullPath(page);
        return PathParser.render(pagePath);
    }
    private void buildIncludeDirective(String pagePathName, String arg) {
        newPageContent.append("\n!include ").append(arg).append(" .").append(pagePathName).append("\n");
    }
}