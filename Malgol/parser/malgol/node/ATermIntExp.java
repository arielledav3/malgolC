/* This file was generated by SableCC (http://www.sablecc.org/). */

package malgol.node;

import malgol.analysis.*;

@SuppressWarnings("nls")
public final class ATermIntExp extends PIntExp
{
    private PTerm _arg_;

    public ATermIntExp()
    {
        // Constructor
    }

    public ATermIntExp(
        @SuppressWarnings("hiding") PTerm _arg_)
    {
        // Constructor
        setArg(_arg_);

    }

    @Override
    public Object clone()
    {
        return new ATermIntExp(
            cloneNode(this._arg_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseATermIntExp(this);
    }

    public PTerm getArg()
    {
        return this._arg_;
    }

    public void setArg(PTerm node)
    {
        if(this._arg_ != null)
        {
            this._arg_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._arg_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._arg_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._arg_ == child)
        {
            this._arg_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._arg_ == oldChild)
        {
            setArg((PTerm) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
