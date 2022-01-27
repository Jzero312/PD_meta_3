package pd.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pd.Database.ConnDB;
import pd.models.Group;
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
public class GroupController
{
    @GetMapping("/myGroups")
    public List<Group> getContacts(@RequestHeader(value = "Authorization", required = false) String token)
    {
        StringBuffer sb= new StringBuffer(token);

        for(int i = 4; i>0;i--)
            sb.deleteCharAt(sb.length()-i);

        try
        {
            Registry r = LocateRegistry.getRegistry();
            Remote remObj = r.lookup("GRDS_Service");
            RemoteGRDSInterface rgi = (RemoteGRDSInterface) remObj;
            rgi.getMyGroups(sb.toString());
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        try
        {
            ConnDB db = new ConnDB();
            return db.getMyGroups(-1, sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping(value = {"myGroups/{name}"})
    public List<Msg> getMsgFromGroup(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable(value = "name", required = false) String name)
    {
        StringBuffer sb= new StringBuffer(token);

        for(int i = 4; i>0;i--)
            sb.deleteCharAt(sb.length()-i);

        try
        {
            Registry r = LocateRegistry.getRegistry();
            Remote remObj = r.lookup("GRDS_Service");
            RemoteGRDSInterface rgi = (RemoteGRDSInterface) remObj;
            rgi.getGroupMsg(sb.toString(), name);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        try
        {
            ConnDB db = new ConnDB();
            return db.getMsgFromGroup(sb.toString(), -1, name, -1);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
