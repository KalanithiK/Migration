
package com.example.Migration;

public class OMSConstant {

    // Base configuration (change this if the region or cluster ID changes)
    public static final String CAMUNDA_TASKLIST_CLUSTER_BASE = "https://ont-1.tasklist.camunda.io/a3bf3c27-2ab6-431b-9888-bfbfadd3f83e";

    // OAuth configuration
    public static final String CLIENT_ID = "0exTXB73U8YDqxSE5oP~TgjnnuPxDmB2";
    public static final String CLIENT_SECRET = "hOYIOPiMzU-PWuQrHKBCud~ErYuxZT4WH90Y.xovR1LvGa0jQTTW6H3VVebYAa6i";
    public static final String TOKEN_URL = "https://login.cloud.camunda.io/oauth/token";
    public static final String GRANT_TYPE = "client_credentials";
    public static final String AUDIENCE = "tasklist.camunda.io";

    // Keycloak config
    public static final String KEYCLOAK_CLIENT_ID = "aaseyainspectionsolution";
    public static final String KEYCLOAK_CLIENT_SECRET = "nTJJR3VDiLS0GQGySp2aBcNUUHRQIUVn";

    // Tasklist API URLs
    public static final String TASKS_ASSIGN_URL = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v1/tasks/";
    public static final String TASKS_SEARCH_URL = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v1/tasks/search";
    public static final String BASE_TASK_URL = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v1/tasks/";
    public static final String COMPLETE_URL = "/complete";
    public static final String SEARCH_TASK_VARIABLES = "/variables/search";
    public static final String GET_TASK_URL = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v1/tasks/";
    public static final String GET_FORM_URL = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v1/forms/";

    public static final String CAMUNDA_DOCUMENT_UPLOAD = CAMUNDA_TASKLIST_CLUSTER_BASE + "/v2/documents";



}

	//    
	 
//		public static final String TASKS_ASSIGN_URL = "http://10.13.1.180:8082/v1/tasks/";
//		public static String CLIENT_ID = "tasklist";
//		public static String TOKEN_URL = "http://10.13.1.180:18080/auth/realms/camunda-platform/protocol/openid-connect/token";
//		public static String CLIENT_SECRET = "XALaRPl5qwTEItdwCMiPS62nVpKs7dL7";
//		public static String GRANT_TYPE = "client_credentials";
//		public static String TASKS_SEARCH_URL = "http://10.13.1.180:8082/v1/tasks/search";
//		public static String BASE_TASK_URL = "http://10.13.1.180:8082/v1/tasks/";
//		public static String COMPLETE_URL = "/complete";
//		public static String SEARCH_TASK_VARIABLES = "/variables/search";
//		public static String GET_TASK_URL = "http://10.13.1.180:8082/v1/tasks/";
//		public static String GET_FORM_URL = "http://10.13.1.180:8082/v1/forms/";
//		public static String KEYCLOAK_CLIENT_ID = "aaseyainspectionsolution";
//		public static String KEYCLOAK_CLIENT_SECRET = "nTJJR3VDiLS0GQGySp2aBcNUUHRQIUVn";
	//}
	 