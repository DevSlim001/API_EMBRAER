import { React, useState } from 'react'
import { useHistory } from 'react-router-dom';
import { FaSignOutAlt, FaRegFileAlt, FaUserEdit, FaMarker, FaHome, FaRegClipboard, FaFilePdf } from 'react-icons/fa'
import { Container, Row, Col, Nav } from 'react-bootstrap'

import "./index.css"

import Header from './../../components/Header';
import Cadastro from './../Cadastro';
import Perfil from './../Perfil';
import Codelist from './../Codelist';
import Delta from './../Delta';
import Full from './../Full';
import Lep from './../Lep';


function Home(){
    const [conteudo, setConteudo] = useState();
    const renderCadastro = () =>(setConteudo(<Cadastro />))
    const renderPerfil = () =>(setConteudo(<Perfil />))
    const renderCodelist = () =>(setConteudo(<Codelist />))
    const renderDelta = () =>(setConteudo(<Delta />))
    const renderFull = () =>(setConteudo(<Full />))
    const renderLep = () =>(setConteudo(<Lep />))

    
    const hist = useHistory();

    const logout = () =>((
        localStorage.removeItem("token"),
        localStorage.removeItem("manterConectado"),
        hist.push("/")
    ))
    return (
        <Container id="home" fluid>
            <Row id="row-header">
                <Header id="header" />
            </Row>
            <Row id="row-body">
                <Col xs={2} id="col-navbar" >
                    <Nav defaultActiveKey="/home" id="nav-menu" className="flex-column">
                        <Nav.Link href="/home"><FaHome /> <span>Home</span></Nav.Link>
                        <Nav.Link onClick={renderCadastro}><FaMarker /><span>Cadastro</span></Nav.Link>
                        <Nav.Link onClick={renderPerfil}><FaUserEdit /><span>Perfil</span></Nav.Link>
                        <Nav.Link onClick={renderCodelist}><FaRegClipboard /><span>Codelist</span></Nav.Link>
                        <Nav.Link onClick={renderDelta}><FaFilePdf /><span>Delta</span></Nav.Link>
                        <Nav.Link onClick={renderFull}><FaFilePdf /><span>Full</span></Nav.Link>
                        <Nav.Link onClick={renderLep}><FaRegFileAlt /><span>LEP</span></Nav.Link>

                        <Nav.Link onClick={logout}><FaSignOutAlt /><span>Logout</span></Nav.Link>
                    </Nav>
                </Col>
                <Col id="col-body">
                    {conteudo}
                </Col>
            </Row>
        </Container>
    )
}

export default Home;
