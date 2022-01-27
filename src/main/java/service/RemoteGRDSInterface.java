package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteGRDSInterface extends Remote
{
    void login(String name) throws RemoteException;
    void nameChange(String name, String newName) throws RemoteException;
    void getContactList(String name) throws RemoteException;
    void deleteContact(String name, String deleted) throws RemoteException;
    void getMyGroups(String name) throws RemoteException;
    void getGroupMsg(String name, String groupName) throws RemoteException;
    void getContactMsg(String name, String name2) throws RemoteException;
}
