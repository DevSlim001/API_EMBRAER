import api from "./../../services/api";

export default async function getRevisoesByCodManual(codManual){
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.get(`/manual/${codManual}/revisoes`,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};