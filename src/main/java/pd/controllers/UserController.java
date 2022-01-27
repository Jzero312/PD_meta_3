package pd.controllers;

import org.springframework.web.bind.annotation.*;

import pd.Database.ConnDB;
import pd.models.Msg;
import pd.models.User;
import service.RemoteGRDSInterface;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.List;

@RestController
public class UserController
{
    @PostMapping("session")
    public User login(@RequestBody User user)
    {
        try
        {
            Registry r = LocateRegistry.getRegistry();
            Remote remObj = r.lookup("GRDS_Service");
            RemoteGRDSInterface rgi = (RemoteGRDSInterface) remObj;
            rgi.login(user.getUsername());
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        try
        {
            ConnDB db = new ConnDB();
            if(db.loginUser(user))
            {
                user.setToken(user.getUsername() + "_123");
                user.setPassword("**********");
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

    @PutMapping("user")
    public String changeName(@RequestHeader (value = "Authorization", required = false) String token, @RequestParam(value = "name", required = true) String newName)
    {
        StringBuffer sb= new StringBuffer(token);

        for(int i = 4; i>0;i--)
            sb.deleteCharAt(sb.length()-i);

        try
        {
            Registry r = LocateRegistry.getRegistry();
            Remote remObj = r.lookup("GRDS_Service");
            RemoteGRDSInterface rgi = (RemoteGRDSInterface) remObj;
            rgi.nameChange(sb.toString(), newName);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }


        try
        {
            ConnDB db = new ConnDB();
            if(db.changeUserName(-1, sb.toString(), newName))
                return "Nome alterado para " + newName;
        } catch (SQLException e) {
            e.printStackTrace();
            return "SQLexception!";
        }
        return "Algo correu mal.";
    }

    @GetMapping("/contacts")
    public List<User> getContacts(@RequestHeader (value = "Authorization", required = false) String token)
    {
        StringBuffer sb= new StringBuffer(token);

        for(int i = 4; i>0;i--)
            sb.deleteCharAt(sb.length()-i);

        try
        {
            Registry r = LocateRegistry.getRegistry();
            Remote remObj = r.lookup("GRDS_Service");
            RemoteGRDSInterface rgi = (RemoteGRDSInterface) remObj;
            rgi.getContactList(sb.toString());
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        try
        {
            ConnDB db = new ConnDB();
            return db.getAllContacts(-1, sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @DeleteMapping("/contacts")
    public String deleteContact(@RequestHeader (value = "Authorization", required = false) String token, @RequestParam(value = "name", required = true) String name) {
        StringBuffer sb = new StringBuffer(token);

        for (int i = 4; i > 0; i--)
            sb.deleteCharAt(sb.length() - i);

        try
        {
            Registry r = LocateRegistry.getRegistry();
            Remote remObj = r.lookup("GRDS_Service");
            RemoteGRDSInterface rgi = (RemoteGRDSInterface) remObj;
            rgi.deleteContact(sb.toString(), name);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        try {
            ConnDB db = new ConnDB();
            if (db.removeContact(sb.toString(), -1, name, -1))
                return "Contacto removido";
        } catch (SQLException e) {
            e.printStackTrace();
            return "SQLexception!";
        }
        return "Algo correu mal.";
    }

    @GetMapping(value = {"contacts/{name}"})
    public List<Msg> getMsgFromContact(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable(value = "name", required = false) String name)
    {
        StringBuffer sb= new StringBuffer(token);

        for(int i = 4; i>0;i--)
            sb.deleteCharAt(sb.length()-i);

        try
        {
            Registry r = LocateRegistry.getRegistry();
            Remote remObj = r.lookup("GRDS_Service");
            RemoteGRDSInterface rgi = (RemoteGRDSInterface) remObj;
            rgi.getContactMsg(sb.toString(), name);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        try
        {
            ConnDB db = new ConnDB();
            return db.getMsgFromContact(sb.toString(), -1, name, -1);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
