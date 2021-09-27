import { React, useState } from 'react'
import { useHistory } from 'react-router-dom';
import { FaSignOutAlt } from 'react-icons/fa'
import { Container, Row, Col, Navbar, Nav } from 'react-bootstrap'

import Header from './../../components/Header';
import Cadastro from './../Cadastro';
import Perfil from './../Perfil';

import "./index.css"

import { FaMarker,FaHome } from "react-icons/fa";


function Home(){
    const [conteudo, setConteudo] = useState();
    const renderCadastro = () =>(setConteudo(<Cadastro />))
    const renderPerfil = () =>(setConteudo(<Perfil />))

    const hist = useHistory();

    const logout = () =>((
        localStorage.removeItem("token"),
        localStorage.removeItem("manterConectado"),
        hist.push("/")
    ))
    return (
        <Container fluid>
            <Row>
                <Header id="header" />
            </Row>
            <Row>
                <Col xs={2} id="navbar-col">
                    <Navbar bg="dark" variant="dark" id="navbar">
                        <Container id="menu-container">
                            <Nav fill className="flex-column" id="menu-nav">
                                <Nav.Link href="#" id="home-nav"><FaHome /> <span>Home</span></Nav.Link>
                                <Nav.Link href="#cadastro" onClick={renderCadastro}><FaMarker /><span>Cadastro</span></Nav.Link>
                                <Nav.Link href="#perfil" onClick={renderPerfil}><FaMarker /><span>Perfil</span></Nav.Link>
                                <Nav.Link onClick={logout} id="logout-nav"><FaSignOutAlt /><span>Logout</span></Nav.Link>

                            </Nav>
                        </Container>
                    </Navbar>
                </Col>
                <Col id="body">
                    <div id="div-body">
                        {conteudo}
                    </div>
                </Col>
            </Row>
        </Container>
    )
}

export default Home;
