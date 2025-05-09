package tests;

import utils.MailUtil;

public class TestMail {
    public static void main(String[] args) {
        MailUtil.reserveEvent(
                "ahmedbousnina02@gmail.com",
                "bousnina.ahmed@esprit.tn",
                "Tech Conference 2025",
                "June 12, 2025"
        );
    }
}
