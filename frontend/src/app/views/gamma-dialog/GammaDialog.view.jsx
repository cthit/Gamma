import React from "react";

import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle
} from "@material-ui/core";

import { DigitIfElseRendering } from "@cthit/react-digit-components";

class GammaDialog extends React.Component {
    state = {
        open: false
    };

    componentDidUpdate(prevProps) {
        if (this.state.open !== this.props.options.open) {
            this.setState({ open: true });
        }
    }

    render() {
        const {
            options,
            gammaDialogClosedCancel,
            gammaDialogClosedConfirm
        } = this.props;

        const { open } = this.state;

        return (
            <Dialog
                open={open}
                onClose={() => {
                    gammaDialogClosedCancel();
                    this.setState({ open: false });
                }}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                    {options != null ? options.title : ""}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        {options != null ? options.description : ""}
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button
                        onClick={() => {
                            gammaDialogClosedCancel();
                            if (options.onCancel != null) {
                                options.onCancel();
                            }
                            this.setState({ open: false });
                        }}
                        color="primary"
                    >
                        {options != null ? options.cancelButtonText : ""}
                    </Button>
                    <Button
                        onClick={() => {
                            gammaDialogClosedConfirm();
                            options.onConfirm();
                            this.setState({ open: false });
                        }}
                        color="primary"
                        autoFocus
                    >
                        {options != null ? options.confirmButtonText : ""}
                    </Button>
                </DialogActions>
            </Dialog>
        );
    }
}

export default GammaDialog;
