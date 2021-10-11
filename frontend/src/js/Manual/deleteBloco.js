import api from "./../../services/api";

export default async function deleteBloco(codManual,codBloco){
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.delete(`/manual/${codManual}/bloco/${codBloco}`,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};