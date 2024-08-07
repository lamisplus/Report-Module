import * as React from 'react';
import Button from '@mui/material/Button';
import ButtonGroup from '@mui/material/ButtonGroup';
import ClickAwayListener from '@mui/material/ClickAwayListener';
import Grow from '@mui/material/Grow';
import Paper from '@mui/material/Paper';
import Popper from '@mui/material/Popper';
import MenuItem from '@mui/material/MenuItem';
import MenuList from '@mui/material/MenuList';
import Typography from '@mui/material/Typography';
import {FaEye, FaCaretDown } from "react-icons/fa";
import Divider from '@mui/material/Divider';
import {useRef} from "react";
import { Link } from 'react-router-dom'



const options = ['Delete', 'Edit'];



export default function SplitActionButton(props) {
    const [open, setOpen] = React.useState(false);
    const anchorRef = useRef(null);
    const [selectedIndex, setSelectedIndex] = React.useState(1);

    const handleClick = () => {
        //console.info(`You clicked ${options[selectedIndex]}`);
    };

    const handleMenuItemClick = () => {
        setOpen(false);
    };

    const handleToggle = () => {
        setOpen((prevOpen) => !prevOpen);
    };

    const handleClose = () => {
        setOpen(false);
    };

    return (
        <React.Fragment>
            <ButtonGroup variant="contained" ref={anchorRef} style={{backgroundColor:'rgb(153, 46, 98)', height:'30px',width:'150px'}} >
                <Link to={props.actions[0].to} style={{borderRight:'20px solid #fff !important'}}>
                    <Button size="small" onClick={handleClick} variant="contained" startIcon={props.actions[0].icon} style={{backgroundColor:'rgb(153, 46, 98)'}}>
                        <Typography variant="h7" style={{fontWeight:'bolder'}}>{props.actions[0].name}</Typography>
                    </Button>
                </Link>

                <Divider orientation="vertical" flexItem style={{borderRight:'20px solid #fff !important', backgroundColor:'#fff',width:'1px', height:'26px',marginTop:'2px'}}>
                    1
                </Divider>
                <Button
                    size="medium"
                    aria-controls={open ? 'split-button-menu' : undefined}
                    aria-expanded={open ? 'true' : undefined}
                    aria-label="select merge strategy"
                    aria-haspopup="menu"
                    onClick={handleToggle}
                    style={{backgroundColor:'rgb(153, 46, 98)'}}
                >
                    <FaCaretDown size='24' />
                </Button>
            </ButtonGroup>
            <Popper
                open={open}
                anchorEl={anchorRef.current}
                role={undefined}
                transition
                disablePortal
                style={{ zIndex:'10000' }}
            >
                {({ TransitionProps, placement }) => (
                    <Grow
                        {...TransitionProps}
                        style={{
                            transformOrigin: placement === 'bottom' ? 'center top' : 'center bottom',
                            zIndex:'1000'
                        }}
                    >
                        {/*width: 130,*/}
                        <Paper sx={{ width:'150px', marginTop:'2px', zIndex:'1000' }}>
                            <ClickAwayListener onClickAway={handleClose}>
                                <MenuList id="split-button-menu" autoFocusItem>
                                    {props.actions.slice(1).map((option, index) => (
                                        <MenuItem
                                            key={Math.random()}
                                            {...(option.type==='button'&& {onClick:option.onClick})}
                                        >
                                            <Link
                                                to={option.type==='link'?option.to:'#'}
                                                style={{width:'100%'}}
                                            >
                                                {option.icon}<span style={{color: 'rgb(153, 46, 98)', fontWeight:'bolder', fontSize:'12px'}}>{option.name}</span>
                                            </Link>

                                        </MenuItem>
                                    ))}
                                </MenuList>
                            </ClickAwayListener>
                        </Paper>
                    </Grow>
                )}
            </Popper>
        </React.Fragment>
    );
}
