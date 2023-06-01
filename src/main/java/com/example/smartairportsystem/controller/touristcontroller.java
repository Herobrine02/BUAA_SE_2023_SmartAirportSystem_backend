package com.example.smartairportsystem.controller;

import com.example.smartairportsystem.entity.person;
import com.example.smartairportsystem.entity.token;
import com.example.smartairportsystem.service.impl.personserviceimpl;
import com.example.smartairportsystem.service.impl.securityserviceimpl;
import com.example.smartairportsystem.service.impl.tokenserviceimpl;
import com.example.smartairportsystem.service.impl.touristserviceimpl;
import com.example.smartairportsystem.service.personservice;
import com.example.smartairportsystem.service.securityservice;
import com.example.smartairportsystem.service.tokenservice;
import com.example.smartairportsystem.service.touristservice;
import com.example.smartairportsystem.entity.tourist;

import com.example.smartairportsystem.utils.TokenTypeUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@CrossOrigin
@RestController
@RequestMapping(value = "/tourist")
public class touristcontroller {
    @Resource
    private touristservice touristService = new touristserviceimpl();
    @Resource
    private tokenservice tokenService = new tokenserviceimpl();
    @Resource
    private securityservice securityService = new securityserviceimpl();
    @Resource
    private personservice personService = new personserviceimpl();

    //旅客用户注册功能
    @RequestMapping(value = "/logup",method = RequestMethod.POST)
    public Map<String,Object> logupTourist(@RequestParam Map<String,String> rawmap){
        Map<String,Object> map = new HashMap<>();

        //表单取参
        String email = rawmap.get("email");
        String passwords = rawmap.get("passwords");
        String repasswords = rawmap.get("repasswords");
        String vip = rawmap.get("vip");

        try{
            if(repasswords.equals(passwords)) {
                //对用户设置的密码加盐加密后保存
                Random root = new Random((new Random()).nextInt());
                String salt = root.nextInt()+"";
                tourist newtourist = new tourist(email,passwords+salt,salt, vip);
                tourist exist = touristService.getTouristByEmail(email);
                if (exist != null) {
                    map.put("success", false);
                    map.put("message", "邮箱已被注册！");
                } else {
                    touristService.logupNewTourist(newtourist);
                    map.put("success", true);
                    map.put("message", "用户注册成功！");
                }
            }else{
                map.put("success", false);
                map.put("message", "确认密码不一致！");
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("success",false);
            map.put("message","用户注册失败！");
        }
        return map;
    }

    //旅客用户登录功能
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Map<String,Object> loginTourist(@RequestParam Map<String,String> rawmap){
        Map<String,Object> map = new HashMap<>();
        map.put("token", "null");

        //表单取参
        String email = rawmap.get("email");
        String passwords = rawmap.get("passwords");

        try{
            tourist exist = touristService.getTouristByEmail(email);
            if (exist != null) {
                //取出用户盐值，与当前输入的密码拼接加密后再与数据库中的信息进行比较
                String inpwd = securityService.SHA1(passwords+exist.getSalt());
                if(inpwd.equals(exist.getPasswords())) {
                    //将用户id经md5加密后作为token一并返回前端，便于后续访问
                    String touristtk = securityService.MD5(exist.getTouristid().toString());
                    token newtk = new token(exist.getTouristid(),touristtk);
                    token existtk = tokenService.getTokenByID(newtk.getId(), TokenTypeUtil.TOURIST);
                    if (existtk == null){
                        tokenService.loginNewToken(newtk, TokenTypeUtil.TOURIST);
                    }else{
                        tokenService.updateOldToken(newtk, TokenTypeUtil.TOURIST);
                    }
                    map.put("success", true);
                    map.put("message", "用户登录成功！");
                    map.put("token",touristtk);
                }else {
                    map.put("success", false);
                    map.put("message", "用户密码错误！");
                }
            } else {
                map.put("success", false);
                map.put("message", "用户名不存在！");
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("success",false);
            map.put("message","用户登录失败！");
        }
        return map;
    }

    //列出该用户的实名信息功能
    @RequestMapping(value = "/listperson",method = RequestMethod.POST)
    public Map<String,Object> listPerson(@RequestParam Map<String,String> rawmap){
        Map<String,Object> map = new HashMap<>();

        //表单取参
        String touristtk = rawmap.get("token");

        try {
            token tokenentity = tokenService.getTokenByToken(touristtk,TokenTypeUtil.TOURIST);
            if(tokenentity == null){
                map.put("success", false);
                map.put("message", "用户未登录或已注销登录！");
            }else {
                List<person> rtlist = personService.listPersonByTouristid(tokenentity.getId());
                map.put("success", true);
                map.put("message", rtlist);
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("success", false);
            map.put("message", "获取列表失败！");
        }
        return map;
    }

    //旅客用户添加实名信息功能
    @RequestMapping(value = "/addperson",method = RequestMethod.POST)
    public Map<String,Object> addPerson(@RequestParam Map<String,String> rawmap){
        Map<String,Object> map = new HashMap<>();

        //表单取参
        String touristtk = rawmap.get("token");
        String realname = rawmap.get("realname");
        String idnumber = rawmap.get("idnumber");
        String email = rawmap.get("email");

        try {
            token tokenentity = tokenService.getTokenByToken(touristtk,TokenTypeUtil.TOURIST);
            if(tokenentity == null){
                map.put("success", false);
                map.put("message", "用户未登录或已注销登录！");
            }else {
                person newperson = new person(tokenentity.getId(),realname,idnumber,email);
                person exist = personService.getPersonByCombine(tokenentity.getId(),idnumber);
                if(exist != null){
                    map.put("success", false);
                    map.put("message", "实名信息已存在！");
                }else{
                    personService.addNewPerson(newperson);
                    map.put("success", true);
                    map.put("message", "添加实名信息成功！");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("success", false);
            map.put("message", "添加实名信息失败！");
        }
        return map;
    }

    //旅客用户修改实名信息功能
    @RequestMapping(value = "/updateperson",method = RequestMethod.POST)
    public Map<String,Object> updatePerson(@RequestParam Map<String,String> rawmap){
        Map<String,Object> map = new HashMap<>();

        //表单取参
        String touristtk = rawmap.get("token");
        String personid = rawmap.get("personid");
        String realname = rawmap.get("realname");
        String idnumber = rawmap.get("idnumber");
        String email = rawmap.get("email");

        try {
            token tokenentity = tokenService.getTokenByToken(touristtk,TokenTypeUtil.TOURIST);
            if(tokenentity == null){
                map.put("success", false);
                map.put("message", "用户未登录或已注销登录！");
            }else {
                person newperson = new person(tokenentity.getId(),realname,idnumber,email);
                newperson.setPersonid(Integer.parseInt(personid));
                person exist = personService.getPersonByID(Integer.parseInt(personid));
                if(exist != null){
                    person conflict = personService.getPersonByCombine(tokenentity.getId(),idnumber);
                    if(conflict != null){
                        map.put("success", false);
                        map.put("message", "已存在相同实名信息！");
                    }else {
                        personService.updateOldPerson(newperson);
                        map.put("success", true);
                        map.put("message", "实名信息已更新！");
                    }
                }else{
                    map.put("success", false);
                    map.put("message", "实名信息不存在！");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("success", false);
            map.put("message", "修改实名信息失败！");
        }
        return map;
    }

    //旅客用户删除实名信息功能
    @RequestMapping(value = "/removeperson",method = RequestMethod.POST)
    public Map<String,Object> removePerson(@RequestParam Map<String,String> rawmap){
        Map<String,Object> map = new HashMap<>();

        //表单取参
        String touristtk = rawmap.get("token");
        String personid = rawmap.get("personid");

        try {
            token tokenentity = tokenService.getTokenByToken(touristtk,TokenTypeUtil.TOURIST);
            if(tokenentity == null){
                map.put("success", false);
                map.put("message", "用户未登录或已注销登录！");
            }else {
                person exist = personService.getPersonByID(Integer.parseInt(personid));
                if(exist != null){
                    personService.removeOldPerson(Integer.parseInt(personid));
                    map.put("success", true);
                    map.put("message", "实名信息已删除！");
                }else{
                    map.put("success", false);
                    map.put("message", "实名信息不存在！");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("success", false);
            map.put("message", "删除实名信息失败！");
        }
        return map;
    }
}
