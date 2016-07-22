/* This file was generated by SableCC (http://www.sablecc.org/). */

package malgol.node;

import malgol.analysis.*;

@SuppressWarnings("nls")
public final class ABlockBlock extends PBlock
{
    private TLBrace _lBrace_;
    private PDeclList _declList_;
    private TSeparator _separator_;
    private PStmtList _stmtList_;
    private TRBrace _rBrace_;

    public ABlockBlock()
    {
        // Constructor
    }

    public ABlockBlock(
        @SuppressWarnings("hiding") TLBrace _lBrace_,
        @SuppressWarnings("hiding") PDeclList _declList_,
        @SuppressWarnings("hiding") TSeparator _separator_,
        @SuppressWarnings("hiding") PStmtList _stmtList_,
        @SuppressWarnings("hiding") TRBrace _rBrace_)
    {
        // Constructor
        setLBrace(_lBrace_);

        setDeclList(_declList_);

        setSeparator(_separator_);

        setStmtList(_stmtList_);

        setRBrace(_rBrace_);

    }

    @Override
    public Object clone()
    {
        return new ABlockBlock(
            cloneNode(this._lBrace_),
            cloneNode(this._declList_),
            cloneNode(this._separator_),
            cloneNode(this._stmtList_),
            cloneNode(this._rBrace_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABlockBlock(this);
    }

    public TLBrace getLBrace()
    {
        return this._lBrace_;
    }

    public void setLBrace(TLBrace node)
    {
        if(this._lBrace_ != null)
        {
            this._lBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lBrace_ = node;
    }

    public PDeclList getDeclList()
    {
        return this._declList_;
    }

    public void setDeclList(PDeclList node)
    {
        if(this._declList_ != null)
        {
            this._declList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._declList_ = node;
    }

    public TSeparator getSeparator()
    {
        return this._separator_;
    }

    public void setSeparator(TSeparator node)
    {
        if(this._separator_ != null)
        {
            this._separator_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._separator_ = node;
    }

    public PStmtList getStmtList()
    {
        return this._stmtList_;
    }

    public void setStmtList(PStmtList node)
    {
        if(this._stmtList_ != null)
        {
            this._stmtList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._stmtList_ = node;
    }

    public TRBrace getRBrace()
    {
        return this._rBrace_;
    }

    public void setRBrace(TRBrace node)
    {
        if(this._rBrace_ != null)
        {
            this._rBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rBrace_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._lBrace_)
            + toString(this._declList_)
            + toString(this._separator_)
            + toString(this._stmtList_)
            + toString(this._rBrace_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._lBrace_ == child)
        {
            this._lBrace_ = null;
            return;
        }

        if(this._declList_ == child)
        {
            this._declList_ = null;
            return;
        }

        if(this._separator_ == child)
        {
            this._separator_ = null;
            return;
        }

        if(this._stmtList_ == child)
        {
            this._stmtList_ = null;
            return;
        }

        if(this._rBrace_ == child)
        {
            this._rBrace_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._lBrace_ == oldChild)
        {
            setLBrace((TLBrace) newChild);
            return;
        }

        if(this._declList_ == oldChild)
        {
            setDeclList((PDeclList) newChild);
            return;
        }

        if(this._separator_ == oldChild)
        {
            setSeparator((TSeparator) newChild);
            return;
        }

        if(this._stmtList_ == oldChild)
        {
            setStmtList((PStmtList) newChild);
            return;
        }

        if(this._rBrace_ == oldChild)
        {
            setRBrace((TRBrace) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
