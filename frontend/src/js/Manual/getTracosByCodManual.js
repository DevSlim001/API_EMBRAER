import api from "./../../services/api";

export default async function getTracosByCodManual(codManual){
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.get(`/manual/${codManual}/tracos`,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};