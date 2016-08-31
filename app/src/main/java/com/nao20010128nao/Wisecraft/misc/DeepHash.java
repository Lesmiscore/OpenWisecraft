package com.nao20010128nao.Wisecraft.misc;

import java.math.*;
import java.security.*;

public class DeepHash 
{
    public byte[] doHash(byte[] data){
        BigInteger sha1;
        try {
            MessageDigest md=MessageDigest.getInstance("sha1");
            byte[] buf=md.digest(data);
            sha1 = new BigInteger(buf);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        BigInteger sha256;
        try {
            MessageDigest md=MessageDigest.getInstance("sha256");
            byte[] buf=md.digest(data);
            sha256 = new BigInteger(buf);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        BigInteger md5;
        try {
            MessageDigest md=MessageDigest.getInstance("md5");
            byte[] buf=md.digest(data);
            md5 = new BigInteger(buf);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        BigInteger multiplied=new BigInteger("0");
        for(int i=0;i<15;i++){
            multiplied=sha1.multiply(sha256.multiply(md5));
            try {
                MessageDigest md=MessageDigest.getInstance("sha1");
                byte[] buf=md.digest(multiplied.toByteArray());
                sha1 = new BigInteger(buf).multiply(multiplied);
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
            try {
                MessageDigest md=MessageDigest.getInstance("sha256");
                byte[] buf=md.digest(multiplied.toByteArray());
                sha256 = new BigInteger(buf).multiply(multiplied);
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
            try {
                MessageDigest md=MessageDigest.getInstance("md5");
                byte[] buf=md.digest(multiplied.toByteArray());
                md5 = new BigInteger(buf).multiply(multiplied);
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        }
        try {
            MessageDigest md=MessageDigest.getInstance("sha256");
            return md.digest(multiplied.multiply(sha1.multiply(sha256.multiply(md5))).toByteArray());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
