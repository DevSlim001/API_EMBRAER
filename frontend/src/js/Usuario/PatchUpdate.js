import api from "./../../services/api";

export default async function patchUpdateUser(data){
    let token = localStorage.getItem("token")
    let config = {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':'application/json-patch+json'
        }
    }
    return await api.patch("/usuarios",data,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};