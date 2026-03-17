package com.ulatina.gestion;

import com.ulatina.gestion.util.JPAUtil;

public class Main {

    public static void main(String[] args) {
        try {

        } finally {
            JPAUtil.close();
        }
    }
}
