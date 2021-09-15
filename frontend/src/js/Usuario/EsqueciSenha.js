import api from "./../../services/api";

export default async function esqueciSenha(email){
    return await api.patch(`/usuarios/senha/${email}`)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};