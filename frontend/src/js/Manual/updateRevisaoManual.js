import api from "./../../services/api";

export default async function updateRevisoes(codManual){
    let token = localStorage.getItem("token")
    let config = {headers: {'Authorization': `Bearer ${token}`}}
    return await api.put(`/manual/${codManual}/revisoes`,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {
        return err
    })
};