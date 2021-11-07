import api from "./../../services/api";

export default async function putTraco(traco){
    console.log(traco)
    let data = {nome: traco.nome}
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.put(`/manual/traco/${traco.codTraco}`,data,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};