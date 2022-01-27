package service;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class GRDS_Service extends UnicastRemoteObject implements  RemoteGRDSInterface {


    protected GRDS_Service() throws RemoteException {}

    @Override
    public void login(String name) throws RemoteException {
        System.out.println("Utilizador " + name + " tentou realizar login");
    }

    @Override
    public void nameChange(String name, String newName) throws RemoteException {
        System.out.println("Utilizador " + name + " pediu para alterar o nome para " + newName);
    }

    @Override
    public void getContactList(String name) throws RemoteException {
        System.out.println("Utilizador " + name + " pediu lista de contactos.");
    }

    @Override
    public void deleteContact(String name, String deleted) throws RemoteException {
        System.out.println("Utilizador " + name + " pediu para remover " + deleted +" da lista de contactos");

    }

    @Override
    public void getMyGroups(String name) throws RemoteException {
        System.out.println("Utilizador " + name + " pediu a lista de groupos a que pertence.");
    }

    @Override
    public void getGroupMsg(String name, String groupName) throws RemoteException {
        System.out.println("Utilizador " + name + "pediu a lista de mensagens do grupo " + groupName);
    }

    @Override
    public void getContactMsg(String name, String name2) throws RemoteException {
        System.out.println("Utilizador " + name + " pediu a lista de mensagens do contacto " + name2);

    }

    public static void main(String[] args)
    {
        try
        {
            GRDS_Service grds = new GRDS_Service();
            //Naming.rebind("rmi://127.0.0.1:1099/timeServer", ts);
            Registry r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            r.rebind("GRDS_Service", grds);

            System.out.println("Server is running....");
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

}
