package com.example.smartairportsystem.controller;

import com.example.smartairportsystem.entity.*;
import com.example.smartairportsystem.entity.bowl.eticket;
import com.example.smartairportsystem.service.*;
import com.example.smartairportsystem.service.impl.*;

import com.example.smartairportsystem.utils.TimeFormatUtil;
import com.example.smartairportsystem.utils.TokenTypeUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

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
    @Resource
    private flightservice flightService = new flightserviceimpl();
    @Resource
    private ticketservice ticketService = new ticketserviceimpl();
    @Resource
    private purchaserecordservice purchaserecordService = new purchaserecordserviceimpl();

    //旅客用户注册功能
    @RequestMapping(value = "/logup",method = RequestMethod.POST)
    public Map<String,Object> logupTourist(@RequestParam Map<String,String> rawmap){
        Map<String,Object> map = new HashMap<>();

        //表单取参
        String email = rawmap.get("email");
        String passwords = rawmap.get("passwords");
        String repasswords = rawmap.get("repasswords");

        try{
            if(repasswords.equals(passwords)) {
                //对用户设置的密码加盐加密后保存
                Random root = new Random((new Random()).nextInt());
                String salt = root.nextInt()+"";
                tourist newtourist = new tourist(email,passwords+salt,salt, "false");
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
                person conflict = personService.getPersonByCombine(tokenentity.getId(),idnumber);
                if(conflict != null){
                    map.put("success", false);
                    map.put("message", "已存在相同实名信息！");
                }else {
                    personService.updateOldPerson(newperson);
                    map.put("success", true);
                    map.put("message", "实名信息已更新！");
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
                personService.removeOldPerson(Integer.parseInt(personid));
                map.put("success", true);
                map.put("message", "实名信息已删除！");
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("success", false);
            map.put("message", "删除实名信息失败！");
        }
        return map;
    }

    //旅客用户查询航班信息功能
    @RequestMapping(value = "/searchflight",method = RequestMethod.POST)
    public Map<String,Object> searchFlight(@RequestParam Map<String,String> rawmap){
        Map<String,Object> map = new HashMap<>();

        //表单取参
        String touristtk = rawmap.get("token");
        String takeofflocation = rawmap.get("takeofflocation");
        String landinglocation = rawmap.get("landinglocation");
        String date = rawmap.get("date");

        try {
            token tokenentity = tokenService.getTokenByToken(touristtk,TokenTypeUtil.TOURIST);
            if(tokenentity == null){
                map.put("success", false);
                map.put("message", "用户未登录或已注销登录！");
            }else {
                List<flight> rtlist = flightService.listFlightByCombine(takeofflocation,landinglocation,date+"%");
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

    //列出该航班的机票信息功能
    @RequestMapping(value = "/listticket",method = RequestMethod.POST)
    public Map<String,Object> listTicket(@RequestParam Map<String,String> rawmap){
        Map<String, Object> map = new HashMap<>();

        //表单取参
        String touristtk = rawmap.get("token");
        String flightid = rawmap.get("flightid");

        try {
            token tokenentity = tokenService.getTokenByToken(touristtk,TokenTypeUtil.TOURIST);
            if(tokenentity == null){
                map.put("success", false);
                map.put("message", "用户未登录或已注销登录！");
            }else {
                List<ticket> rtlist = ticketService.listTicketByFlightid(Integer.parseInt(flightid));
                map.put("success", true);
                map.put("message", rtlist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("message", "获取列表失败！");
        }
        return map;
    }

    //旅客用户购票功能
    @RequestMapping(value = "/purchaseflight",method = RequestMethod.POST)
    public Map<String,Object> purchaseFlight(@RequestParam Map<String,String> rawmap){
        Map<String,Object> map = new HashMap<>();

        //表单取参
        String touristtk = rawmap.get("token");
        String ticketid = rawmap.get("ticketid");
        String personidlist = rawmap.get("personidlist");

        try {
            token tokenentity = tokenService.getTokenByToken(touristtk,TokenTypeUtil.TOURIST);
            if(tokenentity == null){
                map.put("success", false);
                map.put("message", "用户未登录或已注销登录！");
            }else {
                String[] personids = personidlist.split("&");
                //总人数
                int len = personids.length;
                ticket tkt = ticketService.getTicketByID(Integer.parseInt(ticketid));
                int count = purchaserecordService.getCountByTicketid(Integer.parseInt(ticketid));
                if(tkt.getAmount()-count < len){
                    map.put("success", false);
                    map.put("message", "剩余机票不足！");
                }else {
                    for (String personid : personids) {
                        purchaserecordService.addNewRecord(new purchaserecord(Integer.valueOf(personid), Integer.parseInt(ticketid), TimeFormatUtil.getCurrentTime(), "0"));
                    }
                    map.put("success", true);
                    map.put("message", "用户购票成功");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("success", false);
            map.put("message", "用户购票失败！");
        }
        return map;
    }

    //列出已购机票功能
    @RequestMapping(value = "/listpurchase",method = RequestMethod.POST)
    public Map<String,Object> listPurchase(@RequestParam Map<String,String> rawmap){
        Map<String,Object> map = new HashMap<>();

        //表单取参
        String touristtk = rawmap.get("token");

        try {
            token tokenentity = tokenService.getTokenByToken(touristtk,TokenTypeUtil.TOURIST);
            if(tokenentity == null){
                map.put("success", false);
                map.put("message", "用户未登录或已注销登录！");
            }else {
                List<eticket> rtlist = ticketService.listEticketByTouristid(tokenentity.getId());
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

    //旅客用户退票功能
    @RequestMapping(value = "/returnpurchase",method = RequestMethod.POST)
    public Map<String,Object> returnPurchase(@RequestParam Map<String,String> rawmap){
        Map<String,Object> map = new HashMap<>();

        //表单取参
        String touristtk = rawmap.get("token");
        String orderid = rawmap.get("orderid");

        try {
            token tokenentity = tokenService.getTokenByToken(touristtk,TokenTypeUtil.TOURIST);
            if(tokenentity == null){
                map.put("success", false);
                map.put("message", "用户未登录或已注销登录！");
            }else {
                purchaserecordService.removeOldRecord(Integer.parseInt(orderid));
                map.put("success", true);
                map.put("message", "用户退票成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("success", false);
            map.put("message", "用户退票失败！");
        }
        return map;
    }
}
