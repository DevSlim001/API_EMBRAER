import api from "./../../services/api";

export default async function auth(email,senha){
    let data = {email:email,senha:senha}
    return await api.post("/usuarios/auth",data)
    .then((res)=>{
        return res.data
    })
    .catch((err)=> {return err.response})
};