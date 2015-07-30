package com.example.yaginuma.wikinclient;

/**
 * Created by yaginuma on 15/01/13.
 */
public class TestHelper {
    public static String getDummyListResponse() {
        return "{\"pages\":[{\"id\":3,\"title\":\"test\",\"url\":\"http://localhost:3000/test\",\"body\":\"**テストページ3**\",\"extracted_body\":\"\\u003cp\\u003e\\u003cstrong\\u003eテストページ3\\u003c/strong\\u003e\\u003c/p\\u003e\\n\"},{\"id\":2,\"title\":\"2\",\"url\":\"http://localhost:3000/2\",\"body\":\"2\",\"extracted_body\":\"\\u003cp\\u003e2\\u003c/p\\u003e\\n\"},{\"id\":1,\"title\":\"1\",\"url\":\"http://localhost:3000/1\",\"body\":\"1\",\"extracted_body\":\"\\u003cp\\u003e1\\u003c/p\\u003e\\n\"}],\"results_returned\":3}";
    }
}
