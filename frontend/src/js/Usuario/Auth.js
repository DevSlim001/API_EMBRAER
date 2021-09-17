import api from "./../../services/api";

export default async function auth(email,senha,manterConectado){
    let data = {email:email,senha:senha,manterConectado:manterConectado}
    return await api.post("/usuarios/auth",data)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};