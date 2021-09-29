import api from "./../../services/api";

export default async function getUser(){
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.get("/usuarios",config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};