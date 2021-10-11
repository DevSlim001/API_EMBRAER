import api from "./../../services/api";

export default async function createManual(manual){
    let data = {...manual}
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.post("/manual",data,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};